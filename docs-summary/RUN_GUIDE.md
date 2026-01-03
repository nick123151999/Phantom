# 🚀 Phantom 运行指南

## ❗ 如果 Android Studio 无法运行项目

### 问题：运行配置（Run Configuration）消失或无法打开

这是因为删除 Maven 配置后，Android Studio 需要重新同步项目。

---

## 🔧 解决方法

### 方法 1：重新同步 Gradle（推荐）⭐

1. **清理项目缓存**
   ```bash
   cd /Users/ocean/majiabao/support/Phantom
   ./gradlew clean
   ```

2. **在 Android Studio 中：**
   - 点击菜单：`File` → `Invalidate Caches...`
   - 勾选所有选项
   - 点击 `Invalidate and Restart`

3. **重新同步 Gradle**
   - 点击工具栏的 🐘 图标（Sync Project with Gradle Files）
   - 或者点击菜单：`File` → `Sync Project with Gradle Files`

4. **等待同步完成后，运行配置会自动出现**

---

### 方法 2：手动创建运行配置

如果同步后仍然没有运行配置：

1. **点击顶部工具栏的运行配置下拉框**
2. **选择 `Edit Configurations...`**
3. **点击左上角的 `+` 号**
4. **选择 `Android App`**
5. **配置如下：**
   - **Name:** `Host App`
   - **Module:** `Phantom.phantom-sample.host.main`
   - **Launch:** `Default Activity`
6. **点击 `Apply` 和 `OK`**

---

### 方法 3：命令行运行（最可靠）

如果 Android Studio 还是有问题，直接用命令行：

```bash
# 1. 编译并安装宿主应用
./gradlew :phantom-sample:host:installDebug

# 2. 启动应用
adb shell am start -n com.wlqq.phantom.sample/.MainActivity

# 或者一步完成
./gradlew :phantom-sample:host:installDebug && adb shell am start -n com.wlqq.phantom.sample/.MainActivity
```

---

## 📱 完整运行流程

### 1. 编译所有模块

```bash
# 编译宿主应用
./gradlew :phantom-sample:host:assembleDebug

# 编译插件 APK
./gradlew :phantom-sample:plugin-view:assembleDebug
./gradlew :phantom-sample:plugin-component:assembleDebug
```

### 2. 安装宿主应用

```bash
# 方式 1：使用 Gradle
./gradlew :phantom-sample:host:installDebug

# 方式 2：手动安装
adb install -r phantom-sample/host/build/outputs/apk/debug/host-debug.apk
```

### 3. 推送插件 APK 到设备

```bash
# 推送到 SD 卡
adb push phantom-sample/plugin-view/build/outputs/apk/debug/com.wlqq.phantom.plugin.view_1.0.0.apk /sdcard/

adb push phantom-sample/plugin-component/build/outputs/apk/debug/com.wlqq.phantom.plugin.component_1.0.0.0.apk /sdcard/
```

### 4. 启动宿主应用

```bash
# 启动应用
adb shell am start -n com.wlqq.phantom.sample/.MainActivity

# 查看日志
adb logcat | grep -i phantom
```

### 5. 在应用内加载插件

1. 打开宿主应用
2. 点击"安装插件"按钮
3. 选择 `/sdcard/` 目录下的插件 APK
4. 安装完成后，点击"启动插件"

---

## 🎯 快捷命令

### 一键编译 + 安装 + 运行

```bash
# 宿主应用
./gradlew :phantom-sample:host:installDebug && adb shell am start -n com.wlqq.phantom.sample/.MainActivity

# 编译所有插件
./gradlew :phantom-sample:plugin-view:assembleDebug :phantom-sample:plugin-component:assembleDebug

# 推送所有插件
adb push phantom-sample/plugin-view/build/outputs/apk/debug/*.apk /sdcard/ && \
adb push phantom-sample/plugin-component/build/outputs/apk/debug/*.apk /sdcard/
```

### 清理 + 重新编译

```bash
# 完全清理
./gradlew clean

# 重新编译所有
./gradlew build -x test

# 或者只编译需要的
./gradlew :phantom-sample:host:assembleDebug \
          :phantom-sample:plugin-view:assembleDebug \
          :phantom-sample:plugin-component:assembleDebug
```

---

## 🐛 常见问题

### 问题 1：`Module not found` 错误

**原因：** Gradle 同步不完整

**解决：**
```bash
./gradlew clean
# 然后在 Android Studio 中重新同步
```

### 问题 2：`Cannot resolve symbol` 错误

**原因：** IDE 缓存问题

**解决：**
1. `File` → `Invalidate Caches...`
2. 勾选所有选项
3. `Invalidate and Restart`

### 问题 3：运行时找不到类

**原因：** 依赖配置问题

**解决：**
检查 `build.gradle` 中的依赖是否正确：
```groovy
dependencies {
    implementation project(':phantom-host-lib')
    implementation project(':phantom-communication-lib')
}
```

### 问题 4：插件无法加载

**原因：** 插件 APK 路径错误或权限问题

**解决：**
```bash
# 检查文件是否存在
adb shell ls -l /sdcard/*.apk

# 给予存储权限
adb shell pm grant com.wlqq.phantom.sample android.permission.READ_EXTERNAL_STORAGE
adb shell pm grant com.wlqq.phantom.sample android.permission.WRITE_EXTERNAL_STORAGE
```

---

## 📊 生成的文件位置

```
phantom-sample/host/build/outputs/apk/debug/
└── host-debug.apk (7.7 MB)

phantom-sample/plugin-view/build/outputs/apk/debug/
└── com.wlqq.phantom.plugin.view_1.0.0.apk (3.7 MB)

phantom-sample/plugin-component/build/outputs/apk/debug/
└── com.wlqq.phantom.plugin.component_1.0.0.0.apk (11 MB)
```

---

## 🔍 调试技巧

### 查看应用日志

```bash
# 实时查看 Phantom 相关日志
adb logcat | grep -i phantom

# 查看崩溃日志
adb logcat | grep -E "AndroidRuntime|FATAL"

# 清空日志后重新运行
adb logcat -c && adb logcat | grep -i phantom
```

### 查看已安装的插件

```bash
# 进入应用数据目录
adb shell run-as com.wlqq.phantom.sample

# 查看插件文件
adb shell "run-as com.wlqq.phantom.sample ls -l /data/data/com.wlqq.phantom.sample/files/"
```

### 卸载重装

```bash
# 卸载宿主应用（会清除所有数据）
adb uninstall com.wlqq.phantom.sample

# 重新安装
./gradlew :phantom-sample:host:installDebug
```

---

## ✅ 验证项目正常

运行以下命令，如果都成功则项目正常：

```bash
# 1. 清理
./gradlew clean
# ✅ BUILD SUCCESSFUL

# 2. 编译所有模块
./gradlew build -x test
# ✅ BUILD SUCCESSFUL

# 3. 检查 APK 生成
ls -lh phantom-sample/*/build/outputs/apk/debug/*.apk
# ✅ 应该看到 3 个 APK 文件

# 4. 安装到设备
./gradlew :phantom-sample:host:installDebug
# ✅ BUILD SUCCESSFUL

# 5. 启动应用
adb shell am start -n com.wlqq.phantom.sample/.MainActivity
# ✅ 应用启动成功
```

---

## 📚 相关文档

- [HOW_TO_USE.md](HOW_TO_USE.md) - 详细使用指南
- [MAVEN_CLEANUP_SUMMARY.md](MAVEN_CLEANUP_SUMMARY.md) - Maven 清理总结
- [README.md](README.md) - 项目说明

---

**最后更新：** 2026-01-03  
**适用版本：** Phantom 3.1.3+

