# Maven 清理总结

## 🎯 清理目标

将 Phantom 项目从"可发布到 Maven 仓库"模式改为"仅项目内部使用"模式。

---

## ✅ 已删除的内容

### 1. Maven 发布配置

- ❌ 删除 `buildscript/` 整个目录
  - `buildscript/publish_maven.gradle` - Maven 发布脚本
  - `buildscript/publish_local.gradle` - 本地发布脚本（已废弃）
  - `buildscript/publish_bintray.gradle` - Bintray 发布脚本（已废弃）

### 2. 模块中的 Maven 配置

从以下文件中删除 Maven 相关配置：

- `phantom-host-lib/build.gradle`
  - 删除 `ext { POM_ARTIFACT_ID, POM_VERSION_NAME, POM_DESC }`
  - 删除 `apply from: file("${rootDir}/buildscript/publish_maven.gradle")`

- `phantom-plugin-lib/build.gradle`
  - 删除 `ext { POM_ARTIFACT_ID, POM_VERSION_NAME, POM_DESC }`
  - 删除 `apply from: file("${rootDir}/buildscript/publish_maven.gradle")`

- `phantom-communication-lib/build.gradle`
  - 删除 `ext { POM_ARTIFACT_ID, POM_VERSION_NAME, POM_DESC }`
  - 删除 `apply from: file("${rootDir}/buildscript/publish_maven.gradle")`

- `phantom-gradle-plugin/build.gradle`
  - 删除 `ext { POM_ARTIFACT_ID, POM_VERSION_NAME, POM_DESC }`
  - 删除 `apply from: file("${rootDir}/buildscript/publish_maven.gradle")`

- `maven-version/build.gradle`
  - 删除 `ext { POM_ARTIFACT_ID, POM_VERSION_NAME, POM_DESC }`
  - 删除 `apply from: file("${rootDir}/buildscript/publish_maven.gradle")`

### 3. Maven 依赖改为项目依赖

**phantom-host-lib/build.gradle:**
```diff
- implementation "com.wlqq.phantom:maven-version:1.1.0"
- implementation "com.wlqq.phantom:phantom-communication-lib:${phantomVersion.communicationLib}"
+ implementation project(':maven-version')
+ implementation project(':phantom-communication-lib')
```

**phantom-sample/host/build.gradle:**
```diff
- implementation "com.wlqq.phantom:phantom-host-lib:${phantomVersion.hostLib}"
- implementation "com.wlqq.phantom:phantom-communication-lib:${phantomVersion.communicationLib}"
+ implementation project(':phantom-host-lib')
+ implementation project(':phantom-communication-lib')
- configurations.all {
-     resolutionStrategy.dependencySubstitution {
-         substitute module("com.wlqq.phantom:phantom-host-lib:${phantomVersion.hostLib}") using project(":phantom-host-lib")
-     }
- }
```

**phantom-sample/plugin-view/build.gradle:**
```diff
- compileOnly "com.wlqq.phantom:phantom-communication-lib:${phantomVersion.communicationLib}"
- compileOnly "com.wlqq.phantom:phantom-plugin-lib:${phantomVersion.pluginLib}"
+ compileOnly project(':phantom-communication-lib')
+ compileOnly project(':phantom-plugin-lib')
```

**phantom-sample/plugin-component/build.gradle:**
```diff
- compileOnly "com.wlqq.phantom:phantom-communication-lib:${phantomVersion.communicationLib}"
- compileOnly "com.wlqq.phantom:phantom-plugin-lib:${phantomVersion.pluginLib}"
+ compileOnly project(':phantom-communication-lib')
+ compileOnly project(':phantom-plugin-lib')
```

### 4. 禁用 Phantom Gradle Plugin

**build.gradle (root):**
```diff
- classpath 'com.wlqq.phantom:phantom-gradle-plugin:3.1.3'
+ // NOTE: Phantom Gradle Plugin 已禁用（项目内部使用不需要）
+ // classpath 'com.wlqq.phantom:phantom-gradle-plugin:3.1.3'
```

**settings.gradle:**
```diff
- include ':phantom-gradle-plugin'
+ // include ':phantom-gradle-plugin'  // 已禁用：项目内部使用不需要此插件
```

---

## 📦 现在如何使用

### 编译项目

```bash
# 编译所有模块
./gradlew build

# 编译核心库
./gradlew :phantom-host-lib:assembleRelease
./gradlew :phantom-plugin-lib:assembleRelease
```

### 使用方式 1：项目依赖（推荐）

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

### 使用方式 2：AAR/JAR 文件

```bash
# 复制编译产物
cp phantom-host-lib/build/outputs/aar/phantom-host-lib-release.aar your-app/libs/
cp phantom-communication-lib/build/libs/phantom-communication-lib.jar your-app/libs/
```

```groovy
// your-app/build.gradle
repositories {
    flatDir { dirs 'libs' }
}

dependencies {
    implementation(name: 'phantom-host-lib-release', ext: 'aar')
    implementation files('libs/phantom-communication-lib.jar')
}
```

---

## 🎯 优势

### 清理前（Maven 模式）

- ❌ 需要配置 Maven 仓库 URL 和凭证
- ❌ 需要发布到 Maven Local 才能测试
- ❌ 依赖解析复杂（Maven 坐标 vs 项目依赖）
- ❌ 构建配置冗余（POM 元数据、发布脚本）

### 清理后（项目内部模式）

- ✅ 直接使用项目依赖，无需发布
- ✅ 修改代码立即生效，无需重新发布
- ✅ 构建配置简洁清晰
- ✅ 编译速度更快（无发布步骤）
- ✅ 可以直接使用 AAR/JAR 文件

---

## 📊 文件变化统计

| 文件 | 变化 |
|------|------|
| `buildscript/` | 🗑️ 整个目录删除 |
| `phantom-host-lib/build.gradle` | 📝 删除 8 行 Maven 配置 |
| `phantom-plugin-lib/build.gradle` | 📝 删除 8 行 Maven 配置 |
| `phantom-communication-lib/build.gradle` | 📝 删除 8 行 Maven 配置 |
| `phantom-gradle-plugin/build.gradle` | 📝 删除 8 行 Maven 配置 |
| `maven-version/build.gradle` | 📝 删除 8 行 Maven 配置 |
| `phantom-sample/host/build.gradle` | 📝 简化依赖配置 |
| `phantom-sample/plugin-view/build.gradle` | 📝 简化依赖配置 |
| `phantom-sample/plugin-component/build.gradle` | 📝 简化依赖配置 |
| `build.gradle` (root) | 📝 注释 Gradle Plugin |
| `settings.gradle` | 📝 禁用 phantom-gradle-plugin |
| **总计** | **删除约 100+ 行 Maven 相关代码** |

---

## ✅ 验证结果

```bash
$ ./gradlew clean build -x test

BUILD SUCCESSFUL in 30s
416 actionable tasks: 293 executed, 114 from cache, 9 up-to-date
```

**生成的产物：**

```
✅ phantom-host-lib-release.aar       (204 KB)
✅ phantom-plugin-lib-release.aar     (2.4 KB)
✅ phantom-communication-lib.jar      (12 KB)
✅ maven-version.jar                  (13 KB)
```

---

## 📚 相关文档

- [HOW_TO_USE.md](HOW_TO_USE.md) - 使用指南
- [README.md](README.md) - 项目说明
- [CHANGELOG.md](CHANGELOG.md) - 更新日志

---

**清理完成时间：** 2026-01-03  
**清理后项目状态：** ✅ 编译成功，可直接使用

