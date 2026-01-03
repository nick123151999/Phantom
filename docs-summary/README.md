# Phantom 项目文档索引

本文件夹包含 Phantom 插件框架的所有总结和指南文档。

## 📚 文档列表

### 快速开始
- **[QUICK_START.md](QUICK_START.md)** - 快速开始指南，5 分钟上手
- **[HOW_TO_USE.md](HOW_TO_USE.md)** - 详细使用指南，包含构建、安装、运行步骤
- **[RUN_GUIDE.md](RUN_GUIDE.md)** - 运行指南，包含故障排除

### 核心原理
- **[DYNAMIC_LOADING_PRINCIPLE.md](DYNAMIC_LOADING_PRINCIPLE.md)** - 动态加载原理详解（16KB）
  - 插件加载机制
  - ClassLoader 隔离
  - Activity/Service/BroadcastReceiver 代理
  - 资源加载原理

### 配置指南
- **[APP_CONFIG_GUIDE.md](APP_CONFIG_GUIDE.md)** - 应用配置指南
  - 集中式配置管理
  - applicationId 和版本配置
  - 插件配置说明

- **[INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)** - 集成指南
  - 如何将 Phantom 打包为 AAR
  - 如何集成到其他项目
  - 第三方 APK 加载方式

### 升级和修复记录
- **[UPGRADE_TO_LATEST_2026.md](UPGRADE_TO_LATEST_2026.md)** - 2026 年升级总结
  - Gradle 9.2.1
  - AGP 8.13.0
  - Android SDK 36
  - Java 21
  - AndroidX 最新版本

- **[GRADLE_DEPRECATION_FIXES.md](GRADLE_DEPRECATION_FIXES.md)** - Gradle 废弃 API 修复
  - 属性赋值语法
  - 依赖声明语法
  - Task 定义语法

- **[MAVEN_CLEANUP_SUMMARY.md](MAVEN_CLEANUP_SUMMARY.md)** - Maven 清理总结
  - 移除远程 Maven 发布
  - 改用项目依赖

- **[CLEANUP_COMPLETE.md](CLEANUP_COMPLETE.md)** - 代码清理总结
  - 删除 Apache License 头部
  - 删除 bin/ 目录
  - 删除过时文档

### 项目状态
- **[PROJECT_STATUS.md](PROJECT_STATUS.md)** - 当前项目状态
  - 编译状态
  - 功能状态
  - 已知问题

## 🎯 推荐阅读顺序

### 新用户
1. QUICK_START.md - 快速上手
2. HOW_TO_USE.md - 详细使用
3. DYNAMIC_LOADING_PRINCIPLE.md - 理解原理

### 开发者
1. DYNAMIC_LOADING_PRINCIPLE.md - 核心原理
2. APP_CONFIG_GUIDE.md - 配置管理
3. INTEGRATION_GUIDE.md - 集成方案
4. UPGRADE_TO_LATEST_2026.md - 技术栈

### 维护者
1. PROJECT_STATUS.md - 项目状态
2. UPGRADE_TO_LATEST_2026.md - 升级记录
3. GRADLE_DEPRECATION_FIXES.md - 修复记录
4. CLEANUP_COMPLETE.md - 清理记录

## 📝 文档维护

- 所有文档使用 Markdown 格式
- 保持文档简洁、准确、最新
- 重大更新后及时更新相关文档
- 过时文档及时删除或归档

## 🔗 相关链接

- [主 README](../README.md) - 项目主页
- [docs/](../docs/) - API 文档和技术文档
- [phantom-sample/](../phantom-sample/) - 示例代码

---

最后更新：2026-01-03

