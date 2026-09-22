# 热处理中级工考试 App (Liquid Glass 旗舰版)

[![Build & Release APK](https://github.com/zts5202/shiti/actions/workflows/build-apk.yml/badge.svg)](https://github.com/zts5202/shiti/actions/workflows/build-apk.yml)

专为热处理中级工职业技能等级考核打造的高品质移动端题库训练应用，基于物理真实光学折射与色散（Liquid Glass）视觉体系开发，支持单选题、多选题、判断题专项突破，包含490道国家标准题库全真模拟考试系统。

---

## 📱 手机直装 APK 下载方式（无需本地编译）

### 推荐方式一：手机浏览器一键直链极速下载（最稳定，不会变成 .bin 或 .html）
在手机浏览器（Edge、Chrome、自带浏览器）中直接访问以下直链即可自动触发 APK 下载：

👉 **[点击直接下载最新直装包 (app-release.apk)](https://github.com/zts5202/shiti/raw/main/app-release.apk)**

> 📌 **重要下载提示**：
> 1. 请点击上方蓝色直链，或者在仓库文件列表中找到 **`app-release.apk`** 点击后，在打开的页面点击 **「Download raw file」** 或 **「View raw」**。
> 2. **切勿长按文件链接选择“另存为链接”**，长按保存会将 GitHub 的网页 HTML 代码保存下来，导致安装时提示“解析软件包错误”。
> 3. 如果手机浏览器下载后文件名被改为了 `.bin`，只需在手机文件管理器中将其重命名后缀为 `.apk` 即可正常安装。

---

### 推荐方式二：GitHub Releases 官方发布页（带版本记录）
- 访问 [GitHub Releases 页面](https://github.com/zts5202/shiti/releases)
- 点击最新版本的 **Assets** 下的 `app-release.apk` 或 `热处理中级工考试-v1.2.2.apk` 即可下载。

---

### 推荐方式三：GitHub Actions 自动构建产物 (Artifacts)
- 进入 [Actions 页面](https://github.com/zts5202/shiti/actions)
- 点击最新成功的运行记录
- 滚动到页面底部 **Artifacts** 区域，点击 `app-debug-v1.2.2` 即可直接打包下载。

---

## 🛠️ 关于 GitHub Actions 自动构建环境说明

本项目核心依赖的物理级拟真玻璃组件需要 Android API 37（Android 16+），而 GitHub 官方默认 Ubuntu 虚拟机仅预装至 API 34/35。
工作流脚本（`.github/workflows/build-apk.yml`）现已配置全套自动化环境补全机制：
1. **自动引入 Android SDK 37.0**：通过 `android-actions/setup-android@v3` 动态拉取 `platforms;android-37.0`；
2. **静默许可自愈**：通过 `sdkmanager --licenses` 自动接受 SDK 协议；
3. **Gradle 自动按需补齐**：启用 `android.builder.sdkDownload=true`；
4. **V1 + V2 + V3 签名机制**：保证在 Android 7.0 至 16 各品牌手机（华为/小米/OPPO/vivo/荣耀等）上均可无感安装。

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
