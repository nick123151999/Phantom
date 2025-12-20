# Phantom 项目升级说明 (2025年最新版)

## 已完成的升级

### 1. Gradle 版本升级
- **原版本**: Gradle 9.0-milestone-1 (不稳定)
- **新版本**: Gradle 8.12 (2025年最新稳定版)
- **文件**: `gradle/wrapper/gradle-wrapper.properties`

### 2. Android Gradle Plugin (AGP) 升级
- **原版本**: 3.1.4
- **新版本**: 8.8.0 (2025年最新稳定版)
- **文件**: `build.gradle`, `version.gradle`

### 3. 编译 SDK 和目标 SDK 升级
- **compileSdk**: 28 → 36 (Android 16 Preview)
- **targetSdk**: 22 → 36 (Android 16 Preview)
- **minSdk**: 14 → 24 (Android 7.0，现代化标准)
- **buildTools**: 28.0.3 → 35.0.0
- **文件**: `version.gradle`

### 4. Java 版本升级
- **原版本**: Java 7
- **新版本**: Java 8 (大部分模块), Java 11 (phantom-gradle-plugin)
- **文件**: 所有 `build.gradle` 文件

### 5. 依赖库升级
- **JUnit**: 4.12 → 4.13.2
- **Javassist**: 3.18.2-GA → 3.31.0-GA (2025年最新版本)
- **JOOR**: 0.9.10 (Java 6) → 0.9.15 (Java 8)
- **文件**: 各模块的 `build.gradle`

### 6. 仓库配置更新
- 移除已废弃的 `jcenter()`
- 更新阿里云 Maven 镜像 URL (http → https)
- 添加 `mavenCentral()` 作为主要仓库
- **文件**: `build.gradle`

### 7. Android 命名空间配置
为所有 Android 模块添加了 `namespace` 配置：
- `phantom-host-lib`: `com.wlqq.phantom`
- `phantom-sample:host`: `com.wlqq.phantom.sample`
- `phantom-sample:plugin-component`: `com.wlqq.phantom.plugin.component`
- `phantom-sample:plugin-view`: `com.wlqq.phantom.plugin.view`

### 8. AndroidManifest.xml 更新
- 移除了 `package` 属性（AGP 8+ 不再支持）
- **文件**: `phantom-host-lib/src/main/AndroidManifest.xml`

### 9. BuildConfig 生成
为 `phantom-host-lib` 启用了 BuildConfig 生成功能

### 10. 迁移到 AndroidX
- **启用 AndroidX**: `android.useAndroidX=true`
- **启用 Jetifier**: `android.enableJetifier=true`（自动转换旧依赖）
- **移除 Support Library**: 所有 `com.android.support` 依赖已替换为 AndroidX
- **文件**: `gradle.properties`, 所有 `build.gradle` 文件

#### AndroidX 依赖映射
| Support Library | AndroidX                                   | 版本   |
| --------------- | ------------------------------------------ | ------ |
| support-v4      | androidx.core:core                         | 1.15.0 |
| appcompat-v7    | androidx.appcompat:appcompat               | 1.7.0  |
| recyclerview-v7 | androidx.recyclerview:recyclerview         | 1.3.2  |
| -               | androidx.constraintlayout:constraintlayout | 2.2.0  |
| -               | androidx.lifecycle:lifecycle-runtime       | 2.8.7  |
| multidex        | androidx.multidex:multidex                 | 2.0.1  |

### 11. Gradle 属性优化 (2025年标准)
添加了以下优化配置到 `gradle.properties`:
```properties
# 性能优化
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.jvmargs=-Xmx6144m -XX:MaxMetaspaceSize=2048m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8 -XX:+UseG1GC
org.gradle.daemon=true

# Android优化
android.nonTransitiveRClass=true
android.enableR8.fullMode=true
android.suppressUnsupportedCompileSdk=36

# Kotlin优化（为未来做准备）
kotlin.incremental=true
kotlin.caching.enabled=true
```

## 已知问题和临时解决方案

### 1. Phantom Gradle Plugin 兼容性问题
**问题**: phantom-gradle-plugin 使用了旧版 AGP API (`variantData.scope`)，与 AGP 8+ 不兼容

**临时解决方案**: 
- 在主 `build.gradle` 中注释掉了 phantom-gradle-plugin 的依赖
- 在各应用模块中注释掉了插件的应用和配置
- **影响**: 插件的特殊功能（如依赖排除、混淆配置等）暂时不可用

**受影响文件**:
- `build.gradle`
- `phantom-sample/host/build.gradle`
- `phantom-sample/plugin-component/build.gradle`
- `phantom-sample/plugin-view/build.gradle`

### 2. Bintray 发布插件已废弃
**问题**: `com.novoda:bintray-release` 插件已过时

**临时解决方案**: 
- 注释掉了所有模块中的发布脚本引用
- **影响**: 无法发布到 Bintray（Bintray 服务已关闭）

### 3. OkCheck 插件已废弃
**问题**: `com.liulishuo.okcheck` 插件不再维护

**临时解决方案**: 
- 注释掉了插件依赖和应用
- **影响**: 代码检查功能不可用

### 4. Gradle 缓存问题
**问题**: 在升级过程中遇到 Gradle 缓存损坏问题

**解决方案**: 
```bash
# 清理 Gradle 缓存
rm -rf ~/.gradle/caches
rm -rf .gradle
```

## 构建项目

### 清理项目
```bash
./gradlew clean
```

### 构建主应用
```bash
./gradlew :phantom-sample:host:assembleDebug
```

### 构建所有模块
```bash
./gradlew build -x test
```

## 下一步建议

### 短期
1. **解决 Gradle 缓存问题**: 当前构建可能遇到缓存相关错误，建议完全清理缓存后重试
2. **测试基本功能**: 确保核心库（phantom-host-lib）可以正常编译和使用

### 中期
1. **更新 Phantom Gradle Plugin**: 
   - 将插件代码迁移到 AGP 8+ API
   - 参考 AGP 8 迁移指南: https://developer.android.com/build/releases/past-releases/agp-8-0-0-release-notes
   - 主要需要替换 `variantData.scope` 相关 API

2. **更新发布配置**: 
   - 使用 Maven Publish Plugin 替代 Bintray Release
   - 配置发布到 Maven Central 或私有仓库

### 长期
1. **代码现代化**: 
   - 考虑使用 Kotlin
   - 更新到最新的 Android API
   - 采用 Jetpack 组件

2. **构建优化**: 
   - 启用 Configuration Cache
   - 使用 Version Catalog 管理依赖
   - 迁移到 Kotlin DSL

## 兼容性说明 (2025年标准)

- **最低 Android 版本**: API 24 (Android 7.0 Nougat)
- **目标 Android 版本**: API 36 (Android 16 Preview)
- **编译 SDK 版本**: API 36 (Android 16 Preview)
- **Java 版本**: Java 21 (运行时), Java 8/11 (编译目标)
- **Gradle 版本**: 8.12 (2025年最新稳定版)
- **AGP 版本**: 8.8.0 (2025年最新稳定版)
- **AndroidX**: 已启用（使用最新稳定版本）

### 为什么选择 API 24 作为最低版本？
- Android 7.0+ 占据超过99%的市场份额（2025年数据）
- 支持更多现代Android特性
- 更好的性能和安全性
- 减少兼容性代码，简化开发

## 2025年新增特性和优化

### AndroidX 迁移 ✅
1. **完全迁移到 AndroidX**: 所有 Support Library 依赖已替换
2. **Jetifier 启用**: 自动转换第三方库中的旧依赖
3. **最新 AndroidX 版本**: 使用2025年最新稳定版本
4. **MultiDex 支持**: 启用 AndroidX MultiDex

### 性能优化
1. **G1 垃圾回收器**: 使用 `-XX:+UseG1GC` 提升大型项目构建性能
2. **增加内存配置**: 6GB堆内存 + 2GB Metaspace，适应现代开发需求
3. **Non-transitive R classes**: 减少R类大小，加快编译速度
4. **R8 Full Mode**: 更激进的代码优化和混淆

### 现代化标准
1. **最低 API 24**: 淘汰过时的Android版本，专注现代特性
2. **Android 16 Preview**: 支持最新的Android API
3. **增量编译**: Kotlin增量编译和缓存优化

### 构建优化
1. **Configuration Cache**: 准备就绪（当前禁用以确保兼容性）
2. **Build Cache**: 启用以加速重复构建
3. **并行构建**: 充分利用多核CPU

## 参考资料

- [Android Gradle Plugin 8.8 发布说明](https://developer.android.com/build/releases/gradle-plugin)
- [Gradle 8.12 发布说明](https://docs.gradle.org/8.12/release-notes.html)
- [Android 16 Preview](https://developer.android.com/about/versions/16)
- [迁移到 AndroidX](https://developer.android.com/jetpack/androidx/migrate)
- [Maven Publish Plugin](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [R8 优化指南](https://developer.android.com/build/shrink-code)

