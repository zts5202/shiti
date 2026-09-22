# 热处理中级工考试 App (Liquid Glass 旗舰版)

[![Build & Release APK](https://github.com/zts5202/shiti/actions/workflows/build-apk.yml/badge.svg)](https://github.com/zts5202/shiti/actions/workflows/build-apk.yml)

专为热处理中级工职业技能等级考核打造的高品质移动端题库训练应用，基于物理真实光学折射与色散（Liquid Glass）视觉体系开发，支持单选题、多选题、判断题专项突破，包含490道国家标准题库全真模拟考试系统。

---

## 📱 手机直装 APK 下载

您可以通过以下三种方式直接下载安装包，无需在本地编译：

1. **仓库根目录直接下载**：
   - 根目录下已预置最新编译安装包：[`热处理中级工考试-v1.2.1.apk`](./热处理中级工考试-v1.2.1.apk)
2. **GitHub Releases 发布页**：
   - 点击右侧 [Releases](https://github.com/zts5202/shiti/releases) 下载对应版本的 APK 附件。
3. **GitHub Actions 产物 (Artifacts)**：
   - 进入 [Actions 页面](https://github.com/zts5202/shiti/actions) -> 点击最新的一次运行记录 -> 在底部 **Artifacts** 处点击 `app-debug-v1.2.1` 即可下载。

---

## 🛠️ GitHub Actions 自动构建与发布说明

本项目已配置完整的 GitHub Actions 自动化 CI/CD 流程（`.github/workflows/build-apk.yml`）：
- 每次推送代码（Push）或手动触发（workflow_dispatch）时，云端容器将自动执行 JDK 21 环境准备、签名证书自愈并编译生成 Debug APK。
- 编译完成后会自动上传构建产物（Artifacts），并同步推送至 GitHub Releases。

### ⚠️ 重要：如需 GitHub Actions 自动发布到 Releases
GitHub 默认新建仓库可能对 Actions 的写入权限进行了限制。若在 Actions 页面看到 Releases 权限警告，请按以下步骤开启：
1. 打开当前 GitHub 仓库的 **Settings**（设置）标签页；
2. 在左侧菜单找到 **Actions** -> 点击 **General**；
3. 向下滚动至 **Workflow permissions**（工作流权限）；
4. 勾选 **Read and write permissions**（读取与写入权限）；
5. 点击 **Save** 保存即可。

---

## ✨ 核心特性

- **液态玻璃光学美学（Liquid Glass）**：采用多层光谱色散边缘渐变（Chromatic Dispersion Rim）与高光反射线条，剔透折射，消除黑块阴影瑕疵。
- **明暗双模一键切换**：支持极光深色淬火模式与纯净浅色水晶珍珠模式，顶部状态栏专属切换按钮。
- **自定义手机背景壁纸**：支持从手机本地相册或文件夹选取任意图片作为底层背景，自带智能磨砂微膜保证按键与文字清晰易读。
- **完整 490 道题库**：
  - 单选题（240题）
  - 多选题（120题，含选项勾选确认交互）
  - 判断题（130题）
- **四大训练模式**：
  - 顺序练习（1-490题完整刷题）
  - 随机练习（随机打乱题序专项冲刺）
  - 错题巩固（错题自动收录复盘）
  - 重点收藏（随时标记重点工艺题目）
- **全真模拟考场**：100题抽选、60分钟倒计时、交卷即出成绩单、工艺解析与错题报告。
