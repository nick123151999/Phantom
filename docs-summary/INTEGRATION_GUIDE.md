# Phantom 框架集成指南

## 🎯 目标

将 Phantom 打包成工具库/AAR，集成到任意 Android 应用中，实现动态加载第三方 APK 的能力。

---

## 📦 方案一：发布到 Maven 仓库（推荐）

### 1. 发布 Phantom 库到 Maven

```bash
# 发布到本地 Maven 仓库（测试用）
./gradlew publishAllToMavenLocal

# 发布到远程 Maven 仓库（生产用）
./gradlew publishAll
```

### 2. 在你的应用中集成

**Step 1: 添加依赖**

```groovy
// your-app/build.gradle
dependencies {
    // Phantom 核心库
    implementation 'com.wlqq.phantom:phantom-host-lib:3.1.3'
    implementation 'com.wlqq.phantom:phantom-communication-lib:3.1.2'
    
    // AndroidX 依赖（如果你的应用还没有）
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'androidx.core:core:1.15.0'
    implementation 'androidx.multidex:multidex:2.0.1'
}
```

**Step 2: 初始化 Phantom**

```java
// 在你的 Application 类中
public class YourApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 初始化 Phantom
        PhantomCore.getInstance().init(this, new PhantomCore.Config());
    }
}
```

**Step 3: 放置第三方 APK**

```
your-app/
  └─ src/main/assets/
      └─ plugins/
          ├─ third-party-plugin1.apk
          ├─ third-party-plugin2.apk
          └─ third-party-plugin3.apk
```

**Step 4: 安装并启动插件**

```java
// 从 assets 安装插件
PhantomCore.getInstance().installPluginFromAssets("plugins/third-party-plugin1.apk", 
    new InstallPluginCallback() {
        @Override
        public void onSuccess(String packageName) {
            // 安装成功，启动插件
            Intent intent = new Intent();
            intent.setClassName(packageName, "com.example.plugin.MainActivity");
            PhantomCore.getInstance().startActivity(YourActivity.this, intent);
        }
        
        @Override
        public void onFail(int errorCode, String msg) {
            Log.e("Phantom", "安装失败: " + msg);
        }
    });
```

---

## 📦 方案二：使用 AAR 文件

### 1. 生成 AAR 文件

```bash
# 编译生成 AAR
./gradlew :phantom-host-lib:assembleRelease
./gradlew :phantom-communication-lib:assembleRelease

# AAR 文件位置：
# phantom-host-lib/build/outputs/aar/phantom-host-lib-release.aar
# phantom-communication-lib/build/outputs/aar/phantom-communication-lib-release.aar
```

### 2. 复制 AAR 到你的项目

```
your-app/
  └─ libs/
      ├─ phantom-host-lib-release.aar
      └─ phantom-communication-lib-release.aar
```

### 3. 在 build.gradle 中引用

```groovy
// your-app/build.gradle
android {
    // ...
}

dependencies {
    // 引用本地 AAR
    implementation files('libs/phantom-host-lib-release.aar')
    implementation files('libs/phantom-communication-lib-release.aar')
    
    // AndroidX 依赖
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'androidx.core:core:1.15.0'
    implementation 'androidx.multidex:multidex:2.0.1'
}
```

### 4. 使用方式同方案一

---

## 🔧 高级用法

### 动态下载插件

```java
// 从服务器下载 APK
public void downloadAndInstallPlugin(String url, String pluginName) {
    // 1. 下载 APK
    File pluginFile = new File(getFilesDir(), "plugins/" + pluginName + ".apk");
    downloadFile(url, pluginFile);
    
    // 2. 安装插件
    PhantomCore.getInstance().installPluginFromPath(
        pluginFile.getAbsolutePath(),
        new InstallPluginCallback() {
            @Override
            public void onSuccess(String packageName) {
                Toast.makeText(context, "插件安装成功", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onFail(int errorCode, String msg) {
                Toast.makeText(context, "安装失败: " + msg, Toast.LENGTH_SHORT).show();
            }
        }
    );
}
```

### 插件管理

```java
// 获取已安装的插件列表
List<PluginInfo> plugins = PhantomCore.getInstance().getAllPlugins();

// 卸载插件
PhantomCore.getInstance().uninstallPlugin(packageName);

// 更新插件
PhantomCore.getInstance().updatePlugin(newApkPath, packageName);
```

### 插件通信

```java
// 宿主调用插件服务
IPluginService service = PhantomCore.getInstance()
    .getPluginService(packageName, IPluginService.class);
service.doSomething();

// 插件调用宿主服务
IHostService hostService = PhantomServiceManager.getService(IHostService.class);
hostService.getHostData();
```

---

## 📋 第三方 APK 要求

### 1. APK 必须是标准的 Android 应用

- 有效的 AndroidManifest.xml
- 正确的签名
- 符合 Android 规范

### 2. 建议插件继承 Phantom 基类（可选）

如果第三方 APK 是你可以控制的：

```java
// 插件的 Activity 继承 PluginInterceptActivity
public class PluginActivity extends PluginInterceptActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

// 插件的 Application 继承 PluginInterceptApplication
public class PluginApplication extends PluginInterceptApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化插件
    }
}
```

### 3. 如果是完全的第三方 APK（无法修改源码）

Phantom 也支持加载未修改的第三方 APK，但功能会受限：
- ✅ 可以启动 Activity
- ✅ 可以使用基本功能
- ⚠️ 某些高级特性可能不可用（如宿主-插件通信）

---

## 🎨 完整示例

### 你的主应用代码

```java
public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 显示插件列表
        showPluginList();
        
        // 安装按钮
        findViewById(R.id.btn_install).setOnClickListener(v -> {
            installPluginFromAssets("plugins/third-party-app.apk");
        });
    }
    
    private void showPluginList() {
        List<PluginInfo> plugins = PhantomCore.getInstance().getAllPlugins();
        for (PluginInfo plugin : plugins) {
            Log.i("Plugin", "已安装: " + plugin.packageName);
        }
    }
    
    private void installPluginFromAssets(String assetPath) {
        PhantomCore.getInstance().installPluginFromAssets(assetPath,
            new InstallPluginCallback() {
                @Override
                public void onSuccess(String packageName) {
                    // 安装成功，启动插件
                    launchPlugin(packageName);
                }
                
                @Override
                public void onFail(int errorCode, String msg) {
                    Toast.makeText(MainActivity.this, 
                        "安装失败: " + msg, Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void launchPlugin(String packageName) {
        Intent intent = PhantomCore.getInstance()
            .getLaunchIntentForPackage(packageName);
        if (intent != null) {
            PhantomCore.getInstance().startActivity(this, intent);
        }
    }
}
```

---

## ✅ 优势

1. **零侵入**：主应用只需添加依赖，无需修改现有代码
2. **灵活性**：支持 assets、下载、外部存储等多种方式
3. **完全隔离**：插件之间相互独立，不会冲突
4. **热更新**：可以动态下载和更新插件，无需重启应用
5. **兼容性**：支持 Android 7.0 - Android 16（API 24-36）

---

## 🚀 快速开始

1. **发布 Phantom 库**
   ```bash
   ./gradlew publishAllToMavenLocal
   ```

2. **创建你的应用**
   ```bash
   # 添加依赖到 build.gradle
   implementation 'com.wlqq.phantom:phantom-host-lib:3.1.3'
   ```

3. **放置第三方 APK**
   ```
   your-app/src/main/assets/plugins/third-party.apk
   ```

4. **初始化并使用**
   ```java
   PhantomCore.getInstance().init(this, new PhantomCore.Config());
   PhantomCore.getInstance().installPluginFromAssets("plugins/third-party.apk", callback);
   ```

就这么简单！🎉

---

## 📞 技术支持

如有问题，请参考：
- [README.md](README.md) - 项目概述
- [DYNAMIC_LOADING_PRINCIPLE.md](DYNAMIC_LOADING_PRINCIPLE.md) - 技术原理
- [APP_CONFIG_GUIDE.md](APP_CONFIG_GUIDE.md) - 配置指南

