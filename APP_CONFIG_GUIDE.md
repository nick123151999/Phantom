# 应用包名配置指南

## 📦 统一配置说明

项目已实现包名和版本信息的统一配置管理。所有应用的 `applicationId`、版本号等信息都在根目录的 `build.gradle` 文件中集中配置。

## 🎯 配置位置

**文件**: `build.gradle` (项目根目录)

```groovy
ext {
    // ... 其他配置 ...
    
    // 应用包名配置 - 统一管理所有模块的 applicationId
    appConfig = [
            // 宿主应用配置
            hostApplicationId       : "com.wlqq.phantom.sample",
            hostVersionCode         : 1,
            hostVersionName         : "1.0",
            hostLauncherActivity    : "com.wlqq.phantom.sample.MainActivity",
            
            // 插件配置
            pluginComponentId       : "com.wlqq.phantom.plugin.component",
            pluginComponentVersion  : "1.0.0.0",
            pluginComponentVersionCode: 10000,
            
            pluginViewId            : "com.wlqq.phantom.plugin.view",
            pluginViewVersion       : "1.0.0",
            pluginViewVersionCode   : 10000,
    ]
}
```

## 🔧 如何修改包名

### 1. 修改宿主应用包名

只需在 `build.gradle` 中修改以下配置：

```groovy
appConfig = [
    hostApplicationId       : "com.yourcompany.yourapp",  // 修改这里
    hostVersionCode         : 1,
    hostVersionName         : "1.0",
    hostLauncherActivity    : "com.yourcompany.yourapp.MainActivity",  // 同步修改
    // ...
]
```

### 2. 修改插件包名

```groovy
appConfig = [
    // ...
    pluginComponentId       : "com.yourcompany.plugin.component",  // 修改这里
    pluginComponentVersion  : "1.0.0.0",
    pluginComponentVersionCode: 10000,
    
    pluginViewId            : "com.yourcompany.plugin.view",  // 修改这里
    pluginViewVersion       : "1.0.0",
    pluginViewVersionCode   : 10000,
]
```

### 3. 修改版本号

```groovy
appConfig = [
    hostVersionCode         : 2,      // 修改版本号
    hostVersionName         : "2.0",  // 修改版本名称
    // ...
]
```

## ✅ 自动应用的位置

修改 `appConfig` 后，以下位置会自动更新：

### 宿主应用 (`phantom-sample/host/build.gradle`)
```groovy
android {
    namespace appConfig.hostApplicationId
    defaultConfig {
        applicationId appConfig.hostApplicationId
        versionCode appConfig.hostVersionCode
        versionName appConfig.hostVersionName
    }
}
```

### 插件应用 (`phantom-sample/plugin-*/build.gradle`)
```groovy
android {
    namespace appConfig.pluginComponentId  // 或 pluginViewId
    defaultConfig {
        applicationId appConfig.pluginComponentId
        versionCode appConfig.pluginComponentVersionCode
        versionName appConfig.pluginComponentVersion
    }
}
```

### Phantom 插件配置
```groovy
phantomPluginConfig {
    hostApplicationId = appConfig.hostApplicationId
    hostAppLauncherActivity = appConfig.hostLauncherActivity
    pluginApplicationId = android.defaultConfig.applicationId
    pluginVersionName = android.defaultConfig.versionName
}
```

## 🎨 配置优势

1. **集中管理**: 所有包名和版本信息在一个地方配置
2. **避免错误**: 不需要在多个文件中重复修改，减少遗漏
3. **易于维护**: 修改包名只需改一处，所有引用自动更新
4. **版本统一**: 确保所有模块使用一致的版本号

## 📝 注意事项

1. **代码中的引用**: 如果代码中硬编码了包名，需要手动修改
2. **Manifest 文件**: `AndroidManifest.xml` 中的 `package` 属性会自动使用 `namespace` 配置
3. **R 文件**: 资源文件的 R 类会使用 `namespace` 配置的包名
4. **同步 Gradle**: 修改配置后，记得点击 "Sync Project with Gradle Files"

## 🚀 快速开始

要修改整个项目的包名：

1. 打开 `build.gradle` (根目录)
2. 找到 `appConfig` 配置块
3. 修改相应的 `applicationId` 配置
4. 同步 Gradle 项目
5. 重新编译

就这么简单！✨

