# AndroidX 迁移指南

## ✅ 已完成的迁移

项目已成功从 Android Support Library 迁移到 AndroidX！

## 📦 依赖变化

### 已替换的依赖

| 旧依赖 (Support Library)                     | 新依赖 (AndroidX)                    | 版本   |
| -------------------------------------------- | ------------------------------------ | ------ |
| `com.android.support:support-v4:28.0.0`      | `androidx.core:core`                 | 1.15.0 |
| `com.android.support:appcompat-v7:28.0.0`    | `androidx.appcompat:appcompat`       | 1.7.0  |
| `com.android.support:recyclerview-v7:28.0.0` | `androidx.recyclerview:recyclerview` | 1.3.2  |
| `com.android.multidex:multidex:1.0.3`        | `androidx.multidex:multidex`         | 2.0.1  |
| `android.arch.lifecycle:*:1.1.1`             | `androidx.lifecycle:lifecycle-*`     | 2.8.7  |

### 新增的依赖

| 依赖                                         | 版本  | 说明              |
| -------------------------------------------- | ----- | ----------------- |
| `androidx.constraintlayout:constraintlayout` | 2.2.0 | 现代布局系统      |
| `androidx.test.ext:junit`                    | 1.2.1 | AndroidX 测试框架 |
| `androidx.test.espresso:espresso-core`       | 3.6.1 | UI 测试框架       |

## 🔧 配置变化

### gradle.properties
```properties
# 启用 AndroidX
android.useAndroidX=true

# 启用 Jetifier（自动转换第三方库）
android.enableJetifier=true
```

### AndroidManifest.xml
```xml
<!-- 旧的 -->
<application android:name="android.support.multidex.MultiDexApplication">

<!-- 新的 -->
<application android:name="androidx.multidex.MultiDexApplication">
```

或者在 Application 类中：
```java
// 旧的
import android.support.multidex.MultiDex;

// 新的
import androidx.multidex.MultiDex;
```

## 📝 代码迁移

### 需要更新的 import 语句

#### Activity 相关
```java
// 旧的
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

// 新的
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
```

#### RecyclerView
```java
// 旧的
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;

// 新的
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
```

#### ViewPager
```java
// 旧的
import android.support.v4.view.ViewPager;

// 新的
import androidx.viewpager.widget.ViewPager;
// 或使用新版本
import androidx.viewpager2.widget.ViewPager2;
```

#### Context Compat
```java
// 旧的
import android.support.v4.content.ContextCompat;

// 新的
import androidx.core.content.ContextCompat;
```

#### Lifecycle
```java
// 旧的
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.LiveData;

// 新的
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
```

## 🛠️ 自动迁移工具

### 使用 Android Studio 迁移工具

1. **打开项目**
2. **菜单**: Refactor → Migrate to AndroidX
3. **备份项目**: 工具会提示创建备份
4. **自动转换**: 工具会自动转换所有代码和依赖

### 手动迁移步骤

如果自动工具失败，可以手动迁移：

1. **更新 gradle.properties**
   ```properties
   android.useAndroidX=true
   android.enableJetifier=true
   ```

2. **更新所有 build.gradle 文件**
   - 替换所有 Support Library 依赖为 AndroidX

3. **更新 Java/Kotlin 代码**
   - 使用查找替换功能
   - 替换 `android.support.` → `androidx.`
   - 替换 `android.arch.` → `androidx.`

4. **清理并重新构建**
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

## 🔍 常见问题

### Q1: Jetifier 是什么？
**A**: Jetifier 是一个工具，可以自动将第三方库中的 Support Library 依赖转换为 AndroidX。当你的项目使用 AndroidX，但某些第三方库仍使用 Support Library 时，Jetifier 会自动处理这种不兼容。

### Q2: 为什么需要迁移到 AndroidX？
**A**: 
- Support Library 已停止维护
- AndroidX 提供更好的向后兼容性
- 新功能只在 AndroidX 中提供
- Google 官方推荐

### Q3: 迁移后出现编译错误怎么办？
**A**:
1. 清理缓存: `./gradlew clean`
2. 删除 `.gradle` 和 `build` 目录
3. 同步项目: File → Sync Project with Gradle Files
4. 检查是否有遗漏的 import 语句

### Q4: 某些第三方库不兼容怎么办？
**A**:
1. 启用 Jetifier（已启用）
2. 更新第三方库到最新版本
3. 如果库已废弃，寻找 AndroidX 兼容的替代品

## 📊 迁移检查清单

- [x] 更新 `gradle.properties`
- [x] 更新所有 `build.gradle` 文件
- [x] 替换 Support Library 依赖
- [x] 添加 AndroidX 依赖
- [ ] 更新 Java/Kotlin 代码中的 import
- [ ] 更新 XML 布局文件中的组件引用
- [ ] 测试所有功能
- [ ] 运行单元测试
- [ ] 运行 UI 测试

## 🎯 下一步

### 代码更新
由于项目中的 Java 代码仍然使用旧的 import，需要批量替换：

```bash
# 在项目根目录执行
find . -name "*.java" -type f -exec sed -i '' 's/android\.support\.v7\.app/androidx.appcompat.app/g' {} +
find . -name "*.java" -type f -exec sed -i '' 's/android\.support\.v4/androidx/g' {} +
find . -name "*.java" -type f -exec sed -i '' 's/android\.support\.v7\.widget/androidx.recyclerview.widget/g' {} +
```

或者使用 Android Studio 的查找替换功能：
1. Edit → Find → Replace in Files (Cmd+Shift+R / Ctrl+Shift+H)
2. 替换所有 `android.support.` 为对应的 `androidx.` 包名

### 测试
```bash
# 运行单元测试
./gradlew test

# 运行 Android 测试
./gradlew connectedAndroidTest

# 构建所有变体
./gradlew build
```

## 📚 参考资料

- [AndroidX 官方文档](https://developer.android.com/jetpack/androidx)
- [迁移到 AndroidX](https://developer.android.com/jetpack/androidx/migrate)
- [AndroidX 包映射](https://developer.android.com/jetpack/androidx/migrate/class-mappings)
- [Jetifier 文档](https://developer.android.com/studio/command-line/jetifier)

---

**迁移完成日期**: 2025年12月21日
**AndroidX 版本**: 最新稳定版
**状态**: ✅ 依赖已迁移，代码需要更新

