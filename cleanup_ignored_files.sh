#!/bin/bash

# Phantom 项目清理脚本
# 用于删除所有应该被 .gitignore 忽略的文件

echo "🧹 开始清理 Phantom 项目中不应提交的文件..."
echo ""

# 统计变量
deleted_count=0

# 1. 清理 .DS_Store 文件
echo "📁 清理 .DS_Store 文件..."
ds_store_count=$(find . -name ".DS_Store" -type f | wc -l | tr -d ' ')
if [ "$ds_store_count" -gt 0 ]; then
    find . -name ".DS_Store" -type f -delete
    echo "   ✅ 删除了 $ds_store_count 个 .DS_Store 文件"
    deleted_count=$((deleted_count + ds_store_count))
else
    echo "   ✓ 没有找到 .DS_Store 文件"
fi
echo ""

# 2. 清理 .bak 备份文件
echo "📄 清理 .bak 备份文件..."
bak_count=$(find . -name "*.bak" -type f | wc -l | tr -d ' ')
if [ "$bak_count" -gt 0 ]; then
    find . -name "*.bak" -type f -delete
    echo "   ✅ 删除了 $bak_count 个 .bak 文件"
    deleted_count=$((deleted_count + bak_count))
else
    echo "   ✓ 没有找到 .bak 文件"
fi
echo ""

# 3. 清理 bin 目录（Eclipse 编译产物）
echo "🗂️  清理 bin 目录..."
bin_dirs=$(find . -name "bin" -type d -not -path "./.gradle/*" -not -path "./gradle/*" | wc -l | tr -d ' ')
if [ "$bin_dirs" -gt 0 ]; then
    find . -name "bin" -type d -not -path "./.gradle/*" -not -path "./gradle/*" -exec rm -rf {} + 2>/dev/null
    echo "   ✅ 删除了 $bin_dirs 个 bin 目录"
    deleted_count=$((deleted_count + bin_dirs))
else
    echo "   ✓ 没有找到 bin 目录"
fi
echo ""

# 4. 清理 build 目录
echo "🏗️  清理 build 目录..."
echo "   ⚠️  建议使用 ./gradlew clean 命令清理 build 目录"
echo ""

# 5. 清理临时文件
echo "📝 清理临时文件..."
temp_count=$(find . \( -name "*.tmp" -o -name "*.temp" -o -name "*~" \) -type f | wc -l | tr -d ' ')
if [ "$temp_count" -gt 0 ]; then
    find . \( -name "*.tmp" -o -name "*.temp" -o -name "*~" \) -type f -delete
    echo "   ✅ 删除了 $temp_count 个临时文件"
    deleted_count=$((deleted_count + temp_count))
else
    echo "   ✓ 没有找到临时文件"
fi
echo ""

# 6. 清理日志文件
echo "📋 清理日志文件..."
log_count=$(find . -name "*.log" -type f | wc -l | tr -d ' ')
if [ "$log_count" -gt 0 ]; then
    find . -name "*.log" -type f -delete
    echo "   ✅ 删除了 $log_count 个日志文件"
    deleted_count=$((deleted_count + log_count))
else
    echo "   ✓ 没有找到日志文件"
fi
echo ""

# 7. 清理 Vim 交换文件
echo "💾 清理 Vim 交换文件..."
swap_count=$(find . \( -name "*.swp" -o -name "*.swo" \) -type f | wc -l | tr -d ' ')
if [ "$swap_count" -gt 0 ]; then
    find . \( -name "*.swp" -o -name "*.swo" \) -type f -delete
    echo "   ✅ 删除了 $swap_count 个交换文件"
    deleted_count=$((deleted_count + swap_count))
else
    echo "   ✓ 没有找到交换文件"
fi
echo ""

# 总结
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ 清理完成！"
echo "   共删除了 $deleted_count 个文件/目录"
echo ""
echo "💡 建议："
echo "   1. 运行 ./gradlew clean 清理构建产物"
echo "   2. 运行 git status --ignored 查看被忽略的文件"
echo "   3. 运行 git status 确认没有不应提交的文件"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

