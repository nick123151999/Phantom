# ⚡ Phantom 快速启动指南

## 🚨 如果 Android Studio 无法运行项目

### 一键修复（推荐）

```bash
./fix_android_studio.sh
```

然后在 Android Studio 中：
1. `File` → `Invalidate Caches...`
2. 勾选所有选项
3. `Invalidate and Restart`
4. 重启后点击 🐘 图标同步 Gradle

---

## 🚀 命令行快速启动（最可靠）

### 方式 1：一键运行

```bash
# 编译 + 安装 + 启动
./gradlew :phantom-sample:host:installDebug && \
adb shell am start -n com.wlqq.phantom.sample/.MainActivity
```

### 方式 2：分步运行

```bash
# 1. 编译
./gradlew :phantom-sample:host:assembleDebug

# 2. 安装
adb install -r phantom-sample/host/build/outputs/apk/debug/host-debug.apk

# 3. 启动
adb shell am start -n com.wlqq.phantom.sample/.MainActivity
```

---

## 📱 加载插件

### 1. 编译插件 APK

```bash
./gradlew :phantom-sample:plugin-view:assembleDebug \
          :phantom-sample:plugin-component:assembleDebug
```

### 2. 推送到设备

```bash
adb push phantom-sample/plugin-view/build/outputs/apk/debug/*.apk /sdcard/
adb push phantom-sample/plugin-component/build/outputs/apk/debug/*.apk /sdcard/
```

### 3. 在应用内加载

1. 打开宿主应用
2. 点击"安装插件"
3. 选择 `/sdcard/` 下的插件 APK
4. 安装后点击"启动插件"

---

## 🐛 常见问题快速解决

### 问题：Android Studio 运行配置消失

**解决：**
```bash
./fix_android_studio.sh
```

### 问题：编译失败

**解决：**
```bash
./gradlew clean
./gradlew build -x test
```

### 问题：插件无法加载

**解决：**
```bash
# 给予存储权限
adb shell pm grant com.wlqq.phantom.sample android.permission.READ_EXTERNAL_STORAGE
adb shell pm grant com.wlqq.phantom.sample android.permission.WRITE_EXTERNAL_STORAGE
adb shell pm grant com.wlqq.phantom.sample android.permission.POST_NOTIFICATIONS
```

### 问题：应用崩溃

**解决：**
```bash
# 查看日志
adb logcat | grep -E "AndroidRuntime|FATAL|phantom"
```

---

## 📊 验证项目正常

```bash
# 运行验证命令
./gradlew clean build -x test

# 检查 APK
ls -lh phantom-sample/*/build/outputs/apk/debug/*.apk
```

应该看到：
- ✅ `host-debug.apk` (7.7 MB)
- ✅ `com.wlqq.phantom.plugin.view_1.0.0.apk` (3.7 MB)
- ✅ `com.wlqq.phantom.plugin.component_1.0.0.0.apk` (11 MB)

---

## 📚 详细文档

- [RUN_GUIDE.md](RUN_GUIDE.md) - 完整运行指南
- [HOW_TO_USE.md](HOW_TO_USE.md) - 使用说明
- [MAVEN_CLEANUP_SUMMARY.md](MAVEN_CLEANUP_SUMMARY.md) - Maven 清理总结

---

**提示：** 如果遇到任何问题，优先使用命令行运行，这是最可靠的方式！

