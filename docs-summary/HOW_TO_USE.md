# Phantom 使用指南

## 📦 项目结构

```
Phantom/
├── phantom-host-lib/          # 宿主应用核心库 (AAR)
├── phantom-plugin-lib/        # 插件开发库 (AAR)
├── phantom-communication-lib/ # 宿主-插件通信库 (JAR)
├── maven-version/             # 版本比较工具库 (JAR)
└── phantom-sample/            # 示例项目
    ├── host/                  # 宿主应用示例
    ├── plugin-view/           # 插件示例 1
    └── plugin-component/      # 插件示例 2
```

---

## 🚀 快速开始

### 1. 编译项目

```bash
# 编译所有模块
./gradlew build

# 或者只编译核心库
./gradlew :phantom-host-lib:assembleRelease
./gradlew :phantom-plugin-lib:assembleRelease
```

### 2. 生成的产物位置

```
phantom-host-lib/build/outputs/aar/
├── phantom-host-lib-debug.aar      (211 KB)
└── phantom-host-lib-release.aar    (204 KB)

phantom-plugin-lib/build/outputs/aar/
├── phantom-plugin-lib-debug.aar    (2.4 KB)
└── phantom-plugin-lib-release.aar  (2.4 KB)

phantom-communication-lib/build/libs/
└── phantom-communication-lib.jar   (12 KB)

maven-version/build/libs/
└── maven-version.jar               (13 KB)
```

---

## 📱 在你的应用中使用

### 方式 1：直接使用项目依赖（推荐）

如果你的应用和 Phantom 在同一个工作区：

```groovy
// settings.gradle
include ':your-app'
include ':phantom-host-lib'
include ':phantom-communication-lib'

// your-app/build.gradle
dependencies {
    implementation project(':phantom-host-lib')
    implementation project(':phantom-communication-lib')
}
```

### 方式 2：使用 AAR/JAR 文件

1. **复制文件到你的项目**

```bash
# 创建 libs 目录
mkdir -p your-app/libs

# 复制 AAR/JAR 文件
cp phantom-host-lib/build/outputs/aar/phantom-host-lib-release.aar your-app/libs/
cp phantom-communication-lib/build/libs/phantom-communication-lib.jar your-app/libs/
cp maven-version/build/libs/maven-version.jar your-app/libs/
```

2. **配置依赖**

```groovy
// your-app/build.gradle
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    implementation(name: 'phantom-host-lib-release', ext: 'aar')
    implementation files('libs/phantom-communication-lib.jar')
    implementation files('libs/maven-version.jar')
    
    // 必需的 AndroidX 依赖
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'androidx.core:core:1.15.0'
    implementation 'androidx.fragment:fragment:1.8.5'
    implementation 'androidx.preference:preference:1.2.1'
}
```

---

## 🔧 开发插件

### 1. 创建插件项目

```groovy
// plugin-app/build.gradle
apply plugin: 'com.android.application'

android {
    compileSdk 36
    
    defaultConfig {
        applicationId "com.example.myplugin"
        minSdk 21
        targetSdk 36
    }
}

dependencies {
    // 编译时依赖 Phantom 库
    compileOnly project(':phantom-plugin-lib')
    compileOnly project(':phantom-communication-lib')
    compileOnly project(':phantom-host-lib')
    
    // 或者使用 AAR 文件
    // compileOnly files('libs/phantom-plugin-lib-release.aar')
    // compileOnly files('libs/phantom-communication-lib.jar')
}
```

### 2. 插件 Activity 继承

```java
import com.wlqq.phantom.library.proxy.PluginInterceptActivity;

public class MyPluginActivity extends PluginInterceptActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 你的代码...
    }
}
```

### 3. 插件 Application 继承

```java
import com.wlqq.phantom.library.proxy.PluginInterceptApplication;

public class MyPluginApplication extends PluginInterceptApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        // 你的初始化代码...
    }
}
```

---

## 📝 运行示例项目

```bash
# 1. 编译宿主应用
./gradlew :phantom-sample:host:assembleDebug

# 2. 编译插件 APK
./gradlew :phantom-sample:plugin-view:assembleDebug
./gradlew :phantom-sample:plugin-component:assembleDebug

# 3. 安装宿主应用
adb install phantom-sample/host/build/outputs/apk/debug/host-debug.apk

# 4. 推送插件 APK 到设备
adb push phantom-sample/plugin-view/build/outputs/apk/debug/plugin-view-debug.apk /sdcard/
adb push phantom-sample/plugin-component/build/outputs/apk/debug/plugin-component-debug.apk /sdcard/

# 5. 运行宿主应用，在应用内加载插件
```

---

## 🎯 核心 API

### 初始化 Phantom

```java
import com.wlqq.phantom.library.PhantomCore;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PhantomCore.getInstance().init(this);
    }
}
```

### 安装插件

```java
String pluginPath = "/sdcard/plugin.apk";
PhantomCore.getInstance().installPlugin(pluginPath);
```

### 启动插件 Activity

```java
Intent intent = new Intent();
intent.setClassName("com.example.plugin", "com.example.plugin.MainActivity");
PhantomCore.getInstance().startActivity(this, intent);
```

---

## 📚 更多文档

- [组件说明](docs/components.md)
- [插件管理](docs/plugin-management.md)
- [常见问题](docs/faq.md)
- [已知问题](docs/known-issues.md)

---

## ✅ 版本信息

- **Android SDK**: 36
- **Build Tools**: 36.0.0
- **Gradle**: 9.2.1
- **AGP**: 8.13.2
- **Java**: 21
- **AndroidX**: 最新稳定版

---

## 🔄 更新日志

查看 [CHANGELOG.md](CHANGELOG.md)

