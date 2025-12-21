# GitHub 上传忽略规则说明

## 当前 `.gitignore` 规则

### 根目录 `.gitignore`
```gitignore
build/
.idea/
.gradle/
.DS_Store
*.iml
local.properties

# eclipse
.classpath
.project
.settings/
```

### 子模块 `.gitignore`
- **phantom-gradle-plugin/.gitignore**: `.DS_Store`, `/build`, `*.iml`
- **phantom-sample/host/src/main/assets/plugins/.gitignore**: `*.apk`

## 问题分析

### ✅ 已忽略（正确）
1. ✅ `build/` - 构建输出目录
2. ✅ `.idea/` - Android Studio 配置
3. ✅ `.gradle/` - Gradle 缓存
4. ✅ `*.iml` - IntelliJ 模块文件
5. ✅ `local.properties` - 本地 SDK 路径
6. ✅ `.DS_Store` - macOS 系统文件
7. ✅ `*.apk` - 插件 APK 文件（在 assets 目录）

### ⚠️ 缺少的重要规则

1. **Android 构建产物**
   - `*.dex` - DEX 字节码文件
   - `*.ap_` - Android 资源包
   - `*.aab` - Android App Bundle
   - `captures/` - 性能分析文件

2. **敏感文件**
   - `*.jks` / `*.keystore` - 签名密钥文件 ⚠️ **重要！**
   - `google-services.json` - Google 服务配置（可能包含密钥）

3. **日志和临时文件**
   - `*.log` - 日志文件
   - `*.tmp` - 临时文件
   - `*.hprof` - 内存快照

4. **测试相关**
   - `test-results/` - 测试结果
   - `coverage/` - 覆盖率报告

5. **其他平台系统文件**
   - `Thumbs.db` - Windows 缩略图
   - `*~` - Linux 备份文件

## 建议的改进

我已经创建了 `.gitignore.recommended` 文件，包含以下改进：

### 1. 更全面的 Android 规则
```gitignore
*.apk
*.ap_
*.aab
*.dex
*.class
captures/
```

### 2. 安全性增强
```gitignore
*.jks
*.keystore
google-services.json
```

### 3. 跨平台支持
```gitignore
# macOS
.DS_Store
.AppleDouble

# Windows
Thumbs.db
Desktop.ini

# Linux
*~
.directory
```

### 4. Phantom 特定规则
```gitignore
# 插件 APK
phantom-sample/host/src/main/assets/*.apk
phantom-sample/host/src/main/assets/plugins/*.apk

# 生成的依赖文件
**/compile_dependencies.txt
**/provided_dependencies_v2.txt
```

## 使用建议

### 方案 1：替换现有文件（推荐）
```bash
# 备份当前文件
cp .gitignore .gitignore.backup

# 使用新的规则
cp .gitignore.recommended .gitignore

# 清理已跟踪但应忽略的文件
git rm -r --cached .
git add .
git commit -m "Update .gitignore rules"
```

### 方案 2：合并规则
```bash
# 将新规则追加到现有文件
cat .gitignore.recommended >> .gitignore

# 手动编辑去重
vim .gitignore
```

### 方案 3：保持现状
如果当前规则满足需求，可以保持不变。但建议至少添加：
```gitignore
# 添加到现有 .gitignore
*.jks
*.keystore
*.log
*.hprof
```

## 验证忽略规则

### 检查哪些文件会被忽略
```bash
# 检查特定文件
git check-ignore -v path/to/file

# 列出所有被忽略的文件
git status --ignored
```

### 查看已跟踪但应该被忽略的文件
```bash
git ls-files -i --exclude-standard
```

### 清理已跟踪的文件
```bash
# 从 Git 中移除但保留本地文件
git rm --cached <file>

# 移除整个目录
git rm -r --cached <directory>
```

## 特别注意

### 🔒 敏感文件检查清单

在提交前，确保以下文件**不会**被上传：

- [ ] `*.jks` / `*.keystore` - 签名密钥
- [ ] `local.properties` - 本地 SDK 路径
- [ ] `google-services.json` - Google 服务配置
- [ ] 任何包含 API 密钥的配置文件
- [ ] 数据库文件
- [ ] 用户数据文件

### 📦 构建产物检查

确保以下构建产物不被上传：

- [ ] `build/` 目录
- [ ] `*.apk` 文件（除非是发布版本）
- [ ] `*.dex` 文件
- [ ] `*.class` 文件
- [ ] 临时文件和日志

## 当前项目状态

根据项目结构，以下文件/目录应该被忽略但可能已被跟踪：

```bash
# 检查这些路径
build/
.gradle/
*.iml
local.properties
phantom-sample/host/src/main/assets/*.apk
*/build/
```

## 推荐操作

1. **立即执行**（安全相关）：
   ```bash
   # 添加密钥文件忽略规则
   echo "*.jks" >> .gitignore
   echo "*.keystore" >> .gitignore
   git add .gitignore
   git commit -m "Add keystore files to gitignore"
   ```

2. **可选执行**（完整更新）：
   ```bash
   # 使用推荐的完整规则
   cp .gitignore.recommended .gitignore
   git add .gitignore
   git commit -m "Update gitignore with comprehensive rules"
   ```

3. **清理历史**（如果已上传敏感文件）：
   ```bash
   # 使用 BFG Repo-Cleaner 或 git filter-branch
   # 警告：这会重写 Git 历史！
   git filter-branch --force --index-filter \
     'git rm --cached --ignore-unmatch path/to/sensitive/file' \
     --prune-empty --tag-name-filter cat -- --all
   ```

## 总结

- ✅ 当前规则覆盖了基本需求
- ⚠️ 建议添加安全相关规则（密钥文件）
- 💡 推荐使用 `.gitignore.recommended` 获得更全面的保护
- 🔍 定期检查是否有敏感文件被意外提交

