# 热处理中级工考试 App (Liquid Glass 旗舰版)

[![Build & Release APK](https://github.com/zts5202/shiti/actions/workflows/build-apk.yml/badge.svg)](https://github.com/zts5202/shiti/actions/workflows/build-apk.yml)

专为热处理中级工职业技能等级考核打造的高品质移动端题库训练应用，基于物理真实光学折射与色散（Liquid Glass）视觉体系开发，支持单选题、多选题、判断题专项突破，包含490道国家标准题库全真模拟考试系统。

---

## 📱 手机直装 APK 下载方式（无需本地编译）

### 🚀 推荐一：国内手机极速直链（免翻墙、不限速、一键直接下载）
直接在手机自带浏览器、微信或 Edge 中点击以下任一镜像高速下载链接（22.8MB 完整安装包）：

- 🔗 **[极速镜像下载 1 (ghfast 高速镜像 - 推荐)](https://ghfast.top/https://raw.githubusercontent.com/zts5202/shiti/main/app-release.apk)**
- 🔗 **[极速镜像下载 2 (ghproxy 备用镜像)](https://ghproxy.cn/https://raw.githubusercontent.com/zts5202/shiti/main/app-release.apk)**
- 🔗 **[极速镜像下载 3 (纯英文包名防乱码下载)](https://ghfast.top/https://raw.githubusercontent.com/zts5202/shiti/main/HeatTreatmentExam-v1.2.3.apk)**

---

### 🌐 推荐二：GitHub 官方直连下载
- 👉 **[GitHub 官方源文件直链 (app-release.apk)](https://github.com/zts5202/shiti/raw/main/app-release.apk)**
- 👉 **[GitHub Releases 官方发布页](https://github.com/zts5202/shiti/releases)**

> 📌 **手机安装避坑指南**：
> 1. **为什么之前直接在 GitHub 上点下载不了或提示解析错误？**  
>    在手机浏览器中访问 GitHub 网页长按或直接保存有时会误存为 HTML 网页（只有几十KB），或者被网络阻断下载中断，未下载完整便安装就会提示“解析软件包错误”。请使用上方的【国内极速镜像下载】链接，下载满 22.8MB 即可秒装。
> 2. **下载后变成 `.bin` 或 `.zip` 怎么办？**  
>    部分手机浏览器（如部分厂商自带浏览器）为了安全会自动修改后缀，在手机自带“文件管理”或“下载管理”中将后缀名改回为 `.apk` 即可直接点击安装。
> 3. **安装时提示“未知来源应用”？**  
>    点击“允许本次安装”或在手机设置中开启“允许来自此来源的应用”即可。

---

### 📦 推荐三：GitHub Actions 自动构建产物 (Artifacts)
- 进入 [Actions 页面](https://github.com/zts5202/shiti/actions)
- 点击最新绿标成功的构建记录
- 滚动到页面底部 **Artifacts** 区域，点击 `app-debug-v1.2.3` 即可下载。

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
