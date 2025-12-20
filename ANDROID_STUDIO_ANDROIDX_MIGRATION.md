# Android Studio AndroidX 自动迁移指南

## 🎯 使用 Android Studio 自动迁移工具

Android Studio 提供了强大的自动迁移工具，可以一键将整个项目迁移到 AndroidX。

## 📋 迁移前准备

### 1. 备份项目（重要！）
```bash
# 方法1: 使用 Git 提交当前更改
git add .
git commit -m "Before AndroidX migration"

# 方法2: 手动备份整个项目文件夹
cp -r /Users/ocean/majiabao/android/Phantom-master /Users/ocean/majiabao/android/Phantom-master-backup
```

### 2. 确认配置已更新
确保以下文件已经更新（✅ 已完成）：
- ✅ `gradle.properties` - AndroidX 已启用
- ✅ 所有 `build.gradle` - 依赖已更新为 AndroidX
- ✅ Gradle 和 AGP 版本已升级

### 3. 同步项目
在 Android Studio 中：
1. 点击 **File → Sync Project with Gradle Files**
2. 等待同步完成
3. 解决任何同步错误

## 🚀 执行自动迁移

### 步骤 1: 打开迁移工具

1. **打开 Android Studio**
2. **打开项目**: `/Users/ocean/majiabao/android/Phantom-master`
3. **等待索引完成**: 底部状态栏显示 "Indexing..."，等待完成

### 步骤 2: 启动迁移向导

**方法 A: 通过菜单（推荐）**
```
菜单栏 → Refactor → Migrate to AndroidX
```

**方法 B: 通过搜索**
1. 按 `Cmd+Shift+A` (Mac) 或 `Ctrl+Shift+A` (Windows/Linux)
2. 输入 "Migrate to AndroidX"
3. 选择并回车

### 步骤 3: 查看迁移预览

迁移工具会显示一个对话框：

```
┌─────────────────────────────────────────────────┐
│  Migrate to AndroidX                            │
├─────────────────────────────────────────────────┤
│                                                 │
│  This will refactor the current project to     │
│  AndroidX. The following will be updated:      │
│                                                 │
│  • Java/Kotlin source files                    │
│  • XML layout files                            │
│  • Gradle build files                          │
│                                                 │
│  [x] Backup project as zip file                │
│                                                 │
│  ⚠️  This operation cannot be undone!          │
│                                                 │
│  [ Do Refactor ]  [ Cancel ]                   │
└─────────────────────────────────────────────────┘
```

**建议**:
- ✅ 勾选 "Backup project as zip file"
- ✅ 点击 "Do Refactor"

### 步骤 4: 等待迁移完成

迁移过程中，Android Studio 会：
1. **扫描项目**: 查找所有需要更新的文件
2. **创建备份**: 如果勾选了备份选项
3. **更新代码**: 自动替换所有 import 语句
4. **更新 XML**: 更新布局文件中的组件引用
5. **更新 Gradle**: 确保依赖正确

进度显示：
```
Migrating to AndroidX...
[████████████████████░░░░] 75%
Processing: MainActivity.java
```

### 步骤 5: 查看迁移结果

迁移完成后，会显示结果对话框：

```
┌─────────────────────────────────────────────────┐
│  AndroidX Migration Complete                    │
├─────────────────────────────────────────────────┤
│                                                 │
│  ✅ Successfully migrated 45 files              │
│  ⚠️  3 files need manual review                │
│                                                 │
│  Files updated:                                 │
│  • 32 Java files                                │
│  • 8 XML files                                  │
│  • 5 Gradle files                               │
│                                                 │
│  [ View Details ]  [ OK ]                       │
└─────────────────────────────────────────────────┘
```

## 📝 迁移后检查

### 1. 查看更改
```bash
# 查看所有更改
git status
git diff

# 查看具体文件的更改
git diff phantom-sample/host/src/main/java/com/wlqq/phantom/sample/MainActivity.java
```

### 2. 同步项目
```
File → Sync Project with Gradle Files
```

### 3. 清理并重新构建
```bash
# 在终端执行
./gradlew clean
./gradlew build
```

## 🔍 常见的自动更新示例

### Java 文件更新

**更新前：**
```java
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;

public class MainActivity extends AppCompatActivity {
    // ...
}
```

**更新后：**
```java
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

public class MainActivity extends AppCompatActivity {
    // ...
}
```

### XML 文件更新

**更新前：**
```xml
<android.support.v7.widget.RecyclerView
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

**更新后：**
```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

## ⚠️ 需要手动检查的情况

### 1. 第三方库冲突
如果某些第三方库不兼容，需要：
- 更新到最新版本
- 或启用 Jetifier（已启用）

### 2. 自定义 View
检查继承自 Support Library 的自定义 View：
```java
// 需要手动更新
public class CustomView extends android.support.v7.widget.AppCompatTextView {
    // 改为
    // extends androidx.appcompat.widget.AppCompatTextView
}
```

### 3. Manifest 文件
检查 `AndroidManifest.xml` 中的组件引用：
```xml
<!-- 可能需要更新 -->
<application android:name="android.support.multidex.MultiDexApplication">
<!-- 改为 -->
<application android:name="androidx.multidex.MultiDexApplication">
```

## 🐛 常见问题解决

### 问题 1: 找不到 "Migrate to AndroidX" 选项

**原因**: Android Studio 版本太旧

**解决方案**:
1. 更新 Android Studio 到最新版本
2. 或使用命令行工具：
```bash
# 使用 Android Studio 自带的迁移工具
./gradlew :app:assembleDebug --scan
```

### 问题 2: 迁移后编译错误

**解决方案**:
```bash
# 1. 清理项目
./gradlew clean
rm -rf .gradle build */build

# 2. 删除 Android Studio 缓存
# File → Invalidate Caches / Restart → Invalidate and Restart

# 3. 重新同步
# File → Sync Project with Gradle Files

# 4. 重新构建
./gradlew build
```

### 问题 3: Jetifier 警告

如果看到类似警告：
```
WARNING: The option setting 'android.enableJetifier=true' is deprecated.
```

**说明**: 这是正常的，Jetifier 会在未来版本中被移除，但目前仍然需要。

### 问题 4: 某些类找不到

**检查**:
1. 确认 `gradle.properties` 中 `android.useAndroidX=true`
2. 同步项目
3. 查看 [AndroidX 类映射表](https://developer.android.com/jetpack/androidx/migrate/class-mappings)

## ✅ 迁移完成检查清单

完成以下检查确保迁移成功：

- [ ] 所有 Java/Kotlin 文件编译通过
- [ ] 所有 XML 布局文件无错误
- [ ] Gradle 同步成功
- [ ] 应用可以正常构建
- [ ] 应用可以在设备上运行
- [ ] 所有功能正常工作
- [ ] 单元测试通过
- [ ] UI 测试通过

## 🎯 验证迁移

### 1. 编译检查
```bash
./gradlew assembleDebug
```

### 2. 运行测试
```bash
./gradlew test
./gradlew connectedAndroidTest
```

### 3. 安装运行
```bash
./gradlew installDebug
adb shell am start -n com.wlqq.phantom.sample/.MainActivity
```

### 4. 检查日志
```bash
adb logcat | grep -i "phantom"
```

## 📊 迁移统计

迁移完成后，通常会更新：

| 文件类型 | 预计数量 | 说明 |
|---------|---------|------|
| Java 文件 | ~50 个 | 更新 import 语句 |
| XML 文件 | ~20 个 | 更新组件引用 |
| Gradle 文件 | ~7 个 | 已手动更新 |
| Manifest 文件 | ~4 个 | 可能需要更新 |

## 🔄 如果需要回滚

### 方法 1: 使用 Git
```bash
git reset --hard HEAD
git clean -fd
```

### 方法 2: 使用备份
```bash
# 恢复 Android Studio 创建的备份
# 备份位置通常在项目根目录的 .AndroidStudio-backup/
```

### 方法 3: 使用手动备份
```bash
rm -rf /Users/ocean/majiabao/android/Phantom-master
cp -r /Users/ocean/majiabao/android/Phantom-master-backup /Users/ocean/majiabao/android/Phantom-master
```

## 📚 相关资源

- [Android Studio 官方迁移指南](https://developer.android.com/studio/refactor/migrate-to-androidx)
- [AndroidX 概述](https://developer.android.com/jetpack/androidx)
- [类映射表](https://developer.android.com/jetpack/androidx/migrate/class-mappings)
- [Jetifier 文档](https://developer.android.com/studio/command-line/jetifier)

## 💡 最佳实践

1. **小步快跑**: 如果项目很大，可以先迁移一个模块测试
2. **及时提交**: 迁移成功后立即提交到版本控制
3. **充分测试**: 迁移后进行全面的功能测试
4. **更新文档**: 更新项目文档说明已使用 AndroidX
5. **团队通知**: 通知团队成员项目已迁移到 AndroidX

---

**准备好了吗？** 现在打开 Android Studio，按照上述步骤开始迁移吧！🚀

**预计时间**: 5-10 分钟
**难度**: ⭐⭐☆☆☆ (简单)
**成功率**: 95%+

