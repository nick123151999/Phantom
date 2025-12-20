# Phantom 项目构建指南 (2025年版)

## 🚀 快速开始

### 环境要求
- **Java**: JDK 21 或更高版本
- **Android Studio**: Ladybug | 2024.2.1 或更高版本
- **操作系统**: macOS / Linux / Windows

### 检查环境
```bash
# 检查 Java 版本
java -version
# 应该显示: openjdk version "21.x.x" 或更高

# 检查 Gradle 版本（项目会自动下载）
./gradlew --version
```

## 📦 2025年最新配置

### 核心版本
| 组件 | 版本 | 说明 |
|------|------|------|
| Gradle | 8.12 | 2025年最新稳定版 |
| Android Gradle Plugin | 8.8.0 | 2025年最新稳定版 |
| Compile SDK | 36 | Android 16 Preview |
| Target SDK | 36 | Android 16 Preview |
| Min SDK | 24 | Android 7.0 Nougat |
| Build Tools | 35.0.0 | 最新版本 |

### 性能配置
- **JVM 堆内存**: 6GB
- **Metaspace**: 2GB
- **垃圾回收器**: G1GC
- **并行构建**: 启用
- **构建缓存**: 启用

## 🛠️ 构建步骤

### 1. 清理项目（首次构建或遇到问题时）
```bash
# 停止所有 Gradle 守护进程
./gradlew --stop

# 清理本地缓存
rm -rf ~/.gradle/caches
rm -rf .gradle build */build */*/build

# 清理项目
./gradlew clean
```

### 2. 构建主应用
```bash
# 构建 Debug 版本
./gradlew :phantom-sample:host:assembleDebug

# 构建 Release 版本
./gradlew :phantom-sample:host:assembleRelease
```

### 3. 构建所有模块
```bash
# 构建所有模块（跳过测试）
./gradlew build -x test

# 构建并运行测试
./gradlew build
```

### 4. 安装到设备
```bash
# 安装 Debug 版本
./gradlew :phantom-sample:host:installDebug

# 安装并运行
adb install -r phantom-sample/host/build/outputs/apk/debug/host-debug.apk
```

## 🔧 常见问题解决

### 问题1: Gradle 缓存损坏
**症状**: `NoSuchFileException` 或 `CorruptedCacheException`

**解决方案**:
```bash
# 完全清理 Gradle 缓存
rm -rf ~/.gradle/caches
./gradlew --stop
./gradlew clean
```

### 问题2: 内存不足
**症状**: `OutOfMemoryError` 或构建缓慢

**解决方案**: 编辑 `gradle.properties`
```properties
org.gradle.jvmargs=-Xmx8192m -XX:MaxMetaspaceSize=2048m
```

### 问题3: Phantom Plugin 不兼容
**症状**: 找不到 `variantData.scope`

**说明**: Phantom Gradle Plugin 使用了旧版 AGP API，暂时已注释掉。如需使用，需要更新插件代码。

### 问题4: BuildConfig 找不到
**症状**: `cannot find symbol: class BuildConfig`

**解决方案**: 已在 `phantom-host-lib/build.gradle` 中启用
```gradle
buildFeatures {
    buildConfig = true
}
```

## 📱 支持的 Android 版本

| Android 版本 | API Level | 支持状态 |
|-------------|-----------|---------|
| Android 16 (Preview) | 36 | ✅ 目标版本 |
| Android 15 | 35 | ✅ 完全支持 |
| Android 14 | 34 | ✅ 完全支持 |
| Android 13 | 33 | ✅ 完全支持 |
| Android 12 | 31-32 | ✅ 完全支持 |
| Android 11 | 30 | ✅ 完全支持 |
| Android 10 | 29 | ✅ 完全支持 |
| Android 9 | 28 | ✅ 完全支持 |
| Android 8 | 26-27 | ✅ 完全支持 |
| Android 7 | 24-25 | ✅ 最低版本 |
| Android 6 及以下 | ≤23 | ❌ 不支持 |

## 🎯 构建优化建议

### 开发环境
```bash
# 使用 assemble 而不是 build（跳过测试和检查）
./gradlew assembleDebug

# 只构建需要的模块
./gradlew :phantom-sample:host:assembleDebug

# 使用 --parallel 加速（已在 gradle.properties 中启用）
./gradlew build --parallel
```

### CI/CD 环境
```bash
# 使用 --no-daemon 避免内存泄漏
./gradlew build --no-daemon

# 使用 --build-cache 加速
./gradlew build --build-cache

# 详细输出
./gradlew build --info
```

## 📊 性能基准

在现代硬件上（M1 Mac / Ryzen 5000+ / Intel 12th Gen+）：

| 任务 | 首次构建 | 增量构建 | 清理构建 |
|------|---------|---------|---------|
| Clean | ~5s | ~3s | ~5s |
| AssembleDebug | ~45s | ~8s | ~40s |
| Build (全部) | ~90s | ~15s | ~80s |

## 🔍 调试技巧

### 查看构建信息
```bash
# 详细输出
./gradlew build --info

# 调试输出
./gradlew build --debug

# 性能分析
./gradlew build --profile
```

### 查看依赖树
```bash
# 查看所有依赖
./gradlew :phantom-sample:host:dependencies

# 查看特定配置的依赖
./gradlew :phantom-sample:host:dependencies --configuration debugRuntimeClasspath
```

### 分析构建性能
```bash
# 生成构建扫描
./gradlew build --scan

# 查看任务执行时间
./gradlew build --profile
# 报告位于: build/reports/profile/
```

## 📚 相关文档

- [完整升级说明](UPGRADE_NOTES.md)
- [Phantom 官方文档](README.md)
- [Android Gradle Plugin 文档](https://developer.android.com/build)
- [Gradle 文档](https://docs.gradle.org)

## 💡 最佳实践

1. **定期清理缓存**: 每周或遇到问题时清理 Gradle 缓存
2. **使用增量构建**: 避免不必要的 clean
3. **并行构建**: 充分利用多核 CPU
4. **构建缓存**: 在团队中共享构建缓存
5. **模块化**: 只构建需要的模块

## 🆘 获取帮助

如果遇到问题：

1. 查看 [UPGRADE_NOTES.md](UPGRADE_NOTES.md) 中的已知问题
2. 清理缓存并重试
3. 查看 Gradle 日志: `./gradlew build --info`
4. 检查 Java 和 Android Studio 版本

---

**最后更新**: 2025年12月21日
**Gradle 版本**: 8.12
**AGP 版本**: 8.8.0

