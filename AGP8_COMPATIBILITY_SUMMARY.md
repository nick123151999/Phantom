# Phantom AGP 8+ 兼容性修复总结

> **日期**: 2025-12-21  
> **状态**: ⚠️ 部分完成 - 需要进一步工作

---

## ✅ **问题 1: Bintray 发布插件 - 已完全解决**

### **解决方案**

创建了新的 Maven Publish 配置 (`buildscript/publish_maven.gradle`)，完全替代已废弃的 Bintray。

### **功能特性**

✅ 支持 Android Library、Java Library、Groovy Plugin  
✅ 发布到本地 Maven (`~/.m2/repository`)  
✅ 支持配置远程 Maven 仓库  
✅ 完整的 POM 元数据  

### **使用方法**

```bash
# 发布单个模块到本地
./gradlew :phantom-host-lib:publishToMavenLocal

# 发布所有模块到本地
./gradlew publishAllToMavenLocal

# 发布到远程仓库（需先配置 maven.url 等）
./gradlew publish
```

### **已更新的模块**

- ✅ phantom-host-lib
- ✅ phantom-plugin-lib
- ✅ phantom-communication-lib
- ✅ phantom-gradle-plugin
- ✅ maven-version

---

## ⚠️ **问题 2: Phantom Gradle Plugin AGP 8+ 兼容性 - 部分完成**

### **核心问题**

AGP 8.0+ 移除了以下 API：
1. `variant.variantData` 和 `variantData.scope`
2. `android.registerTransform()`
3. `com.android.build.gradle.internal.ide.*` 内部 API

### **已完成的修复**

#### ✅ **1. PhantomHostPlugin.groovy**
- 移除对 `variantData.scope` 的依赖
- 使用 `variant.name.capitalize()` 构建任务名
- 使用 `variant.mergeAssetsProvider` 获取输出目录

#### ✅ **2. PhantomPluginPlugin.groovy**
- 移除对 `variantData.scope` 的依赖
- 使用新的 Provider API
- 暂时禁用 Transform API（需要迁移）

#### ✅ **3. PhantomDebugger.groovy**
- 移除对 `globalScope.androidBuilder` 的依赖
- 直接从 `project.android.sdkDirectory` 获取 ADB 路径

#### ✅ **4. FileGenerator.groovy**
- 使用 `project.buildDir` 替代 `scope.globalScope.intermediatesDir`
- 使用 `variant.name` 替代 `scope.variantConfiguration.dirName`

#### ✅ **5. Maven Publish 配置**
- 修复 Android Library 组件访问问题
- 使用 `afterEvaluate` 确保组件可用

#### ✅ **6. 示例项目修复**
- 修复 `assembleDebug` 任务访问（使用 `findByName`）
- 添加空值检查

### **尚未完成的修复**

#### ❌ **1. CompileDependenciesFileGenerator.java**

**问题**: 使用了已移除的内部 API
```java
import com.android.build.gradle.internal.ide.ArtifactDependencyGraph;  // ❌ 已移除
import com.android.build.gradle.internal.ide.ModelBuilder;             // ❌ 已移除
import com.google.common.collect.ImmutableMap;                         // ❌ Guava 依赖
```

**需要的修复**:
- 使用公开的 Gradle API 获取依赖信息
- 通过 `variant.compileConfiguration` 或 `variant.runtimeConfiguration` 获取依赖
- 移除对内部 API 的依赖

#### ❌ **2. ProvidedDependenciesFileGenerator.java**

**问题**: 可能也使用了类似的内部 API（需要检查）

#### ❌ **3. ExcludeClassesTransform.groovy**

**问题**: 使用了已移除的 Transform API
```groovy
project.android.registerTransform(new ExcludeClassesTransform(project))  // ❌ 已移除
```

**需要的修复**:
- 迁移到 Artifact API
- 使用 `androidComponents.onVariants()` 和 `variant.artifacts`

#### ❌ **4. ReplaceSuperTransform.groovy**

**问题**: 同样使用了 Transform API

---

## 📋 **当前功能状态**

| 功能 | 状态 | 说明 |
|------|------|------|
| Maven 发布 | ✅ 完全可用 | 已替换 Bintray |
| 生成依赖清单 | ❌ 编译失败 | 需要修复 CompileDependenciesFileGenerator |
| 快速安装插件 | ⚠️ 部分可用 | 基础功能已修复，但依赖清单生成失败 |
| 自动排除公共库 | ❌ 不可用 | 需要迁移到 Artifact API |
| 替换基类 | ❌ 不可用 | 需要迁移到 Artifact API |

---

## 🔧 **推荐的修复方案**

### **方案 1: 简化版（推荐用于快速恢复功能）**

**目标**: 让核心功能快速可用，暂时放弃高级功能

1. **修复 CompileDependenciesFileGenerator**
   - 使用 Gradle 公开 API 获取依赖
   - 示例代码：
   ```java
   Configuration compileClasspath = variant.getCompileClasspath();
   compileClasspath.getResolvedConfiguration().getResolvedArtifacts().forEach(artifact -> {
       ModuleVersionIdentifier id = artifact.getModuleVersion().getId();
       String coordinates = id.getGroup() + ":" + id.getName() + ":" + id.getVersion();
       // 处理依赖...
   });
   ```

2. **暂时禁用 Transform 功能**
   - 在文档中说明需要手动配置 ProGuard
   - 提供 ProGuard 规则示例

3. **测试基本功能**
   - 生成依赖清单
   - 快速安装插件
   - 动态加载 APK

### **方案 2: 完整版（长期解决方案）**

**目标**: 完全兼容 AGP 8+，恢复所有功能

1. **迁移到 Artifact API**
   ```groovy
   androidComponents.onVariants { variant ->
       variant.artifacts.use(taskProvider)
           .wiredWithDirectories(
               taskProvider.inputDir,
               taskProvider.outputDir
           )
           .toTransform(SingleArtifact.CLASSES)
   }
   ```

2. **重写依赖分析逻辑**
   - 使用 `ResolvableDependencies` API
   - 避免使用任何内部 API

3. **完整测试**
   - 所有功能端到端测试
   - 多个 AGP 版本兼容性测试

---

## 📚 **参考资料**

- [AGP 8.0 Migration Guide](https://developer.android.com/build/releases/past-releases/agp-8-0-0-release-notes)
- [Transform API Migration](https://developer.android.com/build/releases/past-releases/agp-7-0-0-release-notes#transform-api)
- [Artifact API Guide](https://developer.android.com/build/extend-agp)
- [Gradle Dependency Resolution](https://docs.gradle.org/current/userguide/dependency_resolution.html)

---

## 🎯 **下一步行动**

### **立即可做的**

1. ✅ 使用新的 Maven Publish 发布库到本地
2. ⚠️ 修复 CompileDependenciesFileGenerator（方案 1）
3. ⚠️ 测试基本的插件加载功能

### **后续计划**

1. 迁移 Transform API 到 Artifact API
2. 完整的 AGP 8+ 兼容性测试
3. 更新文档和示例

---

## 💡 **临时解决方案**

如果需要立即使用 Phantom，可以：

1. **使用核心功能**（不依赖 Gradle Plugin）
   ```java
   // 直接使用 PhantomCore API
   PhantomCore.getInstance().installPlugin("/path/to/plugin.apk");
   ```

2. **手动管理依赖**
   - 在插件中使用 `compileOnly` 声明宿主提供的库
   - 手动配置 ProGuard 规则排除公共库

3. **手动安装插件**
   - 使用 ADB 命令推送插件 APK
   - 使用广播触发安装

---

## ✅ **已完成的工作总结**

1. ✅ 完全替换 Bintray 为 Maven Publish
2. ✅ 修复 PhantomHostPlugin 基本结构
3. ✅ 修复 PhantomPluginPlugin 基本结构
4. ✅ 修复 PhantomDebugger ADB 路径获取
5. ✅ 修复 FileGenerator 输出目录
6. ✅ 修复示例项目任务访问
7. ✅ 创建详细的文档和说明

**预计完成度**: 约 70%

**剩余工作**: 主要是依赖分析和 Transform API 迁移

---

## 🤝 **需要帮助？**

如果需要继续完成剩余工作，请：

1. 决定使用方案 1（快速）还是方案 2（完整）
2. 提供具体的使用场景和需求
3. 我可以继续完成 CompileDependenciesFileGenerator 的修复

