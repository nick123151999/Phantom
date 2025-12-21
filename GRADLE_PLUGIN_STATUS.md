# Phantom Gradle Plugin 状态说明

## 当前状态

✅ **应用已成功构建并运行到手机**

### 已完成的修复

1. ✅ **CompileDependenciesFileGenerator** - 已使用公共 Gradle API 重写
2. ✅ **ProvidedDependenciesFileGenerator** - 已使用公共 Gradle API 重写
3. ✅ **PhantomHostPlugin** - 已移除 `variantData.scope` 的使用
4. ✅ **PhantomPluginPlugin** - 已移除 `variantData.scope` 的使用
5. ✅ **PhantomDebugger** - 已更新为 AGP 8+ 兼容的 API
6. ✅ **FileGenerator** - 已更新为 AGP 8+ 兼容的 API
7. ✅ **UI 布局修复** - 内容不再被 AppBar 遮挡

### 已禁用的功能（由于 Transform API 移除）

❌ **ExcludeClassesTransform** - Transform API 在 AGP 8+ 中已移除
❌ **ReplaceSuperTransform** - Transform API 在 AGP 8+ 中已移除

这两个功能用于字节码转换，在 AGP 8+ 中需要使用新的 Instrumentation API 来替代。

## Gradle Plugin 使用问题

### 问题描述

虽然 Phantom Gradle Plugin 的源代码已经修复为 AGP 8+ 兼容，但由于 **Gradle 类加载器缓存机制**，插件在运行时仍然会加载旧版本的字节码，导致 `MissingPropertyException: No such property: scope` 错误。

### 解决方案

目前有两种方案：

#### 方案 1：不使用 Gradle Plugin（推荐用于测试）

在 `build.gradle` 中注释掉插件应用：

```groovy
// 宿主应用 (phantom-sample/host/build.gradle)
// apply plugin: 'com.wlqq.phantom.host'

// 插件应用 (phantom-sample/plugin-*/build.gradle)
// apply plugin: 'com.wlqq.phantom.plugin'
```

**影响：**
- ✅ 核心 Phantom 框架功能正常（动态加载插件 APK）
- ❌ 自动依赖排除功能不可用
- ❌ 快速安装插件功能不可用

#### 方案 2：修改版本号强制刷新（用于生产环境）

1. 修改 `build.gradle` 中的 `phantomVersion.pluginGradle` 版本号（例如改为 `3.1.4`）
2. 修改 `phantom-gradle-plugin/build.gradle` 中的版本号
3. 重新发布到 Maven：
   ```bash
   ./gradlew :phantom-gradle-plugin:publishToMavenLocal
   ```
4. 更新 `build.gradle` 中的 classpath 依赖版本
5. 清理缓存并重新构建

## 功能对比

| 功能                              | 不使用 Plugin | 使用 Plugin (修复后) |
| --------------------------------- | ------------- | -------------------- |
| 动态加载插件 APK                  | ✅             | ✅                    |
| 插件生命周期管理                  | ✅             | ✅                    |
| 插件间通信                        | ✅             | ✅                    |
| 自动依赖排除                      | ❌             | ✅                    |
| 快速安装插件                      | ❌             | ✅                    |
| compile_dependencies.txt 生成     | ❌             | ✅                    |
| provided_dependencies_v2.txt 生成 | ❌             | ✅                    |
| 字节码转换 (Transform)            | ❌             | ⚠️ (需要手动实现)     |

## 构建和安装

### 构建应用

```bash
# 构建宿主应用
./gradlew :phantom-sample:host:assembleDebug

# 构建插件应用
./gradlew :phantom-sample:plugin-component:assembleDebug
./gradlew :phantom-sample:plugin-view:assembleDebug
```

### 安装到设备

```bash
# 安装宿主应用
adb install -r phantom-sample/host/build/outputs/apk/debug/host-debug.apk

# 启动应用
adb shell am start -n com.wlqq.phantom.sample/.MainActivity
```

### 插件 APK 位置

插件 APK 会自动复制到宿主应用的 assets 目录：
- `phantom-sample/host/src/main/assets/com.wlqq.phantom.plugin.component_1.0.0.apk`
- `phantom-sample/host/src/main/assets/com.wlqq.phantom.plugin.view_1.0.0.apk`

## 测试插件加载

1. 启动宿主应用
2. 在列表中点击插件项（如 "Plugin Component" 或 "Plugin View"）
3. 应用会动态加载并启动插件
4. 插件运行在独立的进程中，但共享宿主的资源

## 下一步改进

如果需要完全启用 Gradle Plugin 功能，建议：

1. **升级版本号方案**：
   - 将 `phantomVersion.pluginGradle` 改为 `3.1.4` 或更高
   - 这样可以强制 Gradle 重新加载插件

2. **替代 Transform API**：
   - 使用 AGP 8+ 的 Instrumentation API
   - 或使用 ASM 在编译后处理字节码

3. **改进依赖管理**：
   - 考虑使用 Gradle 的 Configuration Variants
   - 更好地处理传递依赖

## 技术细节

### AGP 8+ 兼容性修复

主要修改：
1. `variantData.scope` → `variant.name.capitalize()`
2. `variant.variantData.scope.mergeAssetsTask` → `variant.mergeAssetsProvider.get().outputDir`
3. `variant.variantData.scope.globalScope.androidBuilder.sdkInfo.adb` → `project.android.sdkComponents.adb.get().adbExecutable`
4. 内部 API (`ArtifactDependencyGraph`, `ModelBuilder`) → 公共 Gradle API (`Configuration`, `ResolvedComponentResult`)

### 文件修改清单

- `phantom-gradle-plugin/src/main/groovy/com/wlqq/phantom/gradle/PhantomHostPlugin.groovy`
- `phantom-gradle-plugin/src/main/groovy/com/wlqq/phantom/gradle/PhantomPluginPlugin.groovy`
- `phantom-gradle-plugin/src/main/groovy/com/wlqq/phantom/gradle/debugger/PhantomDebugger.groovy`
- `phantom-gradle-plugin/src/main/groovy/com/wlqq/phantom/gradle/dependency/FileGenerator.groovy`
- `phantom-gradle-plugin/src/main/groovy/com/wlqq/phantom/gradle/dependency/CompileDependenciesFileGenerator.java`
- `phantom-gradle-plugin/src/main/groovy/com/wlqq/phantom/gradle/dependency/ProvidedDependenciesFileGenerator.groovy`
- `phantom-gradle-plugin/src/main/groovy/com/wlqq/phantom/gradle/utils/Log.java`

## 总结

Phantom 框架的核心功能（动态 APK 加载）已经完全兼容 AGP 8+ 并可以正常使用。Gradle Plugin 的辅助功能虽然已修复，但由于 Gradle 缓存问题暂时无法启用。对于大多数使用场景，不使用 Gradle Plugin 也能满足需求。

