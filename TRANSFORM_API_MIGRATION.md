# Transform API 迁移指南 (AGP 8+)

## 问题说明

AGP 8.0 移除了 Transform API，Phantom 的两个 Transform 需要迁移：

1. **ExcludeClassesTransform** - 排除指定的类、资源和 SO 库
2. **ReplaceSuperTransform** - 替换组件父类（Activity/Service/Fragment）

## 解决方案

### 方案 1：使用 AGP 8+ Instrumentation API（推荐）

AGP 8+ 提供了新的 Instrumentation API 来替代 Transform API。

#### 1. 创建 AsmClassVisitorFactory

```kotlin
// phantom-gradle-plugin/src/main/kotlin/com/wlqq/phantom/gradle/transform/PhantomClassVisitorFactory.kt
import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor

abstract class PhantomClassVisitorFactory : AsmClassVisitorFactory<PhantomClassVisitorFactory.Parameters> {
    
    interface Parameters : InstrumentationParameters {
        @get:Input
        val excludeClasses: Property<Set<String>>
        
        @get:Input
        val replaceSuperClass: Property<Boolean>
    }
    
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return PhantomClassVisitor(
            nextClassVisitor,
            parameters.get().excludeClasses.get(),
            parameters.get().replaceSuperClass.get()
        )
    }
    
    override fun isInstrumentable(classData: ClassData): Boolean {
        // 排除 android.* 包
        return !classData.className.startsWith("android.")
    }
}
```

#### 2. 创建 ClassVisitor

```kotlin
// phantom-gradle-plugin/src/main/kotlin/com/wlqq/phantom/gradle/transform/PhantomClassVisitor.kt
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class PhantomClassVisitor(
    nextVisitor: ClassVisitor,
    private val excludeClasses: Set<String>,
    private val replaceSuperClass: Boolean
) : ClassVisitor(Opcodes.ASM9, nextVisitor) {
    
    private var currentClassName: String? = null
    private var originalSuperName: String? = null
    
    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<String>?
    ) {
        currentClassName = name
        originalSuperName = superName
        
        // 替换父类
        val newSuperName = if (replaceSuperClass) {
            getReplacedSuperClass(superName)
        } else {
            superName
        }
        
        super.visit(version, access, name, signature, newSuperName, interfaces)
    }
    
    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return if (replaceSuperClass) {
            ReplaceSuperMethodVisitor(mv, originalSuperName, getReplacedSuperClass(originalSuperName))
        } else {
            mv
        }
    }
    
    private fun getReplacedSuperClass(superName: String?): String? {
        return when (superName) {
            "android/app/Activity",
            "android/support/v4/app/FragmentActivity",
            "android/support/v7/app/AppCompatActivity",
            "android/preference/PreferenceActivity" -> 
                "com/wlqq/phantom/library/proxy/PluginInterceptActivity"
            
            "android/app/Application" -> 
                "com/wlqq/phantom/library/proxy/PluginInterceptApplication"
            
            "android/app/Service" -> 
                "com/wlqq/phantom/library/proxy/PluginInterceptService"
            
            "android/app/IntentService" -> 
                "com/wlqq/phantom/library/proxy/PluginInterceptIntentService"
            
            "android/app/Fragment" -> 
                "com/wlqq/phantom/library/proxy/SysFragmentProxy"
            
            "android/app/DialogFragment" -> 
                "com/wlqq/phantom/library/proxy/SysDialogFragmentProxy"
            
            "android/app/ListFragment" -> 
                "com/wlqq/phantom/library/proxy/SysListFragmentProxy"
            
            "android/preference/PreferenceFragment" -> 
                "com/wlqq/phantom/library/proxy/SysPreferenceFragmentProxy"
            
            else -> superName
        }
    }
}

class ReplaceSuperMethodVisitor(
    nextVisitor: MethodVisitor,
    private val oldSuper: String?,
    private val newSuper: String?
) : MethodVisitor(Opcodes.ASM9, nextVisitor) {
    
    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean
    ) {
        // 替换 super 方法调用
        val newOwner = if (opcode == Opcodes.INVOKESPECIAL && owner == oldSuper) {
            newSuper ?: owner
        } else {
            owner
        }
        super.visitMethodInsn(opcode, newOwner, name, descriptor, isInterface)
    }
}
```

#### 3. 在 Plugin 中注册

```groovy
// phantom-gradle-plugin/src/main/groovy/com/wlqq/phantom/gradle/PhantomPluginPlugin.groovy

void apply(Project project) {
    // ... 现有代码 ...
    
    if (project.plugins.hasPlugin(AppPlugin)) {
        def android = project.extensions.getByType(AppExtension)
        
        // 注册 Instrumentation
        android.androidComponents {
            onVariants(selector().all()) { variant ->
                variant.instrumentation.transformClassesWith(
                    PhantomClassVisitorFactory.class,
                    InstrumentationScope.ALL
                ) { params ->
                    params.excludeClasses.set(config.excludeClasses)
                    params.replaceSuperClass.set(true)
                }
                
                variant.instrumentation.setAsmFramesComputationMode(
                    FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
                )
            }
        }
    }
}
```

### 方案 2：使用 Gradle Task + ASM（临时方案）

如果不想使用 Kotlin，可以创建自定义 Task：

```groovy
// phantom-gradle-plugin/src/main/groovy/com/wlqq/phantom/gradle/task/BytecodeTransformTask.groovy

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*
import org.objectweb.asm.*
import java.util.jar.*

abstract class BytecodeTransformTask extends DefaultTask {
    
    @InputDirectory
    abstract DirectoryProperty getInputDir()
    
    @OutputDirectory
    abstract DirectoryProperty getOutputDir()
    
    @TaskAction
    void transform() {
        def inputDir = inputDir.get().asFile
        def outputDir = outputDir.get().asFile
        
        // 遍历所有 class 文件
        inputDir.eachFileRecurse { file ->
            if (file.name.endsWith('.class')) {
                transformClass(file, outputDir)
            }
        }
    }
    
    private void transformClass(File inputFile, File outputDir) {
        def reader = new ClassReader(inputFile.bytes)
        def writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        def visitor = new PhantomClassVisitor(writer)
        
        reader.accept(visitor, ClassReader.EXPAND_FRAMES)
        
        // 写入输出文件
        def relativePath = inputFile.absolutePath - inputDir.get().asFile.absolutePath
        def outputFile = new File(outputDir, relativePath)
        outputFile.parentFile.mkdirs()
        outputFile.bytes = writer.toByteArray()
    }
}
```

### 方案 3：使用 ProGuard/R8 规则（最简单）

对于排除类的功能，可以使用 ProGuard/R8 规则：

```proguard
# phantom-sample/plugin-*/proguard-rules.pro

# 排除指定的类
-dontwarn com.example.excluded.**
-keep class !com.example.excluded.** { *; }

# 排除指定的资源
-keepresources !**/excluded_resource.xml
```

## 推荐实施步骤

### 短期方案（立即可用）

1. **禁用字节码转换功能**
   - 当前状态：已禁用
   - 影响：插件需要手动继承 Phantom 的代理类

2. **使用 ProGuard 规则排除类**
   - 在插件的 `proguard-rules.pro` 中添加排除规则
   - 可以满足大部分排除需求

### 长期方案（完整功能）

1. **迁移到 Kotlin + Instrumentation API**
   - 创建上述的 `PhantomClassVisitorFactory` 和 `PhantomClassVisitor`
   - 在 `PhantomPluginPlugin` 中注册
   - 测试所有字节码转换功能

2. **更新文档**
   - 说明新的使用方式
   - 提供迁移指南

## 手动实现指南（临时方案）

如果暂时不想修改 Gradle Plugin，可以让插件开发者手动实现：

### 1. 继承 Phantom 代理类

**之前（自动转换）：**
```java
public class MyActivity extends AppCompatActivity {
    // 自动被转换为继承 PluginInterceptActivity
}
```

**现在（手动继承）：**
```java
import com.wlqq.phantom.library.proxy.PluginInterceptActivity;

public class MyActivity extends PluginInterceptActivity {
    // 手动继承代理类
}
```

### 2. 手动排除依赖

**在插件的 build.gradle 中：**
```groovy
dependencies {
    // 使用 compileOnly 排除宿主已有的库
    compileOnly 'com.android.support:appcompat-v7:28.0.0'
    compileOnly 'com.android.support:recyclerview-v7:28.0.0'
    
    // 插件特有的依赖使用 implementation
    implementation 'com.some.library:specific:1.0.0'
}
```

## 工作量评估

| 方案 | 工作量 | 优点 | 缺点 |
|------|--------|------|------|
| 手动实现 | 0 天 | 立即可用 | 需要开发者手动处理 |
| ProGuard 规则 | 0.5 天 | 简单有效 | 功能有限 |
| Gradle Task + ASM | 2-3 天 | 完全控制 | 需要维护 |
| Instrumentation API | 3-5 天 | 官方支持，长期稳定 | 需要学习新 API |

## 总结

**当前状态：** 字节码转换功能已禁用，但核心插件化功能不受影响。

**推荐方案：**
1. **短期**：使用手动继承 + ProGuard 规则
2. **长期**：迁移到 Instrumentation API

**影响范围：**
- ✅ 核心插件加载功能正常
- ⚠️ 需要手动继承 Phantom 代理类
- ⚠️ 需要手动配置依赖排除

如果需要完整的字节码转换功能，建议投入 3-5 天时间实现 Instrumentation API 方案。

