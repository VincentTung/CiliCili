# 🎬 嘻哩嘻哩 (CiliCili)

[![Flutter](https://img.shields.io/badge/Flutter-3.32.4-blue.svg)](https://flutter.dev/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.x-green.svg)](https://spring.io/projects/spring-boot)
[![Android](https://img.shields.io/badge/Android-Native-orange.svg)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

![图标](https://raw.githubusercontent.com/VincentTung/CiliCili/main/art/ic_launcher.png)

## 📖 项目介绍

**嘻哩嘻哩(CiliCili)** 是一款现代化的视频应用，采用前后端分离架构开发。

### ✨ 主要特性

- 🎥 **多端支持**: Flutter跨平台 + Android原生开发
- 🔐 **安全认证**: 集成JWT + Shiro安全框架
- 🚀 **高性能**: Redis缓存 + MyBatis持久层
- 🎨 **现代化UI**: Material Design + 自定义主题
- 📱 **响应式设计**: 适配多种屏幕尺寸
- 🔄 **实时功能**: WebSocket实时通信

### 📅 更新日志

- **2025/07**: 新增 Android Native客户端
- **2025/06**: 适配Flutter 3.32.4

> **Just For Fun** 🎉

## 📱 项目演示视频

🎬 **项目功能演示**：[点击观看完整演示视频](https://www.bilibili.com/video/BV1fM8gzQE7t/)

> 通过演示视频可以更直观地了解项目的功能和界面效果

## 🛠️ 技术栈

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.x | 主框架 |
| Shiro | - | 安全框架 |
| MyBatis | - | ORM框架 |
| MySQL | - | 数据库 |
| Redis | - | 缓存 |
| JWT | - | 身份认证 |

### 前端技术栈

#### Flutter版本
```yaml
框架: Flutter 3.32.4
状态管理: GetX
网络请求: Dio
视频播放: video_player
动画: Lottie
JSON处理: json_annotation
```

#### Android Native版本
```yaml
架构模式: MVVM
依赖注入: Hilt
网络请求: Retrofit
图片加载: Glide
UI框架: Compose
动画: Lottie
```

## 🚀 快速开始

### 环境要求

- Flutter 3.32.4+
- Android Studio / VS Code
- Java 8+
- MySQL 5.7+
- Redis 5.0+

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/VincentTung/CiliCili.git
cd CiliCili
```

2. **后端启动**
```bash
cd source/server/funvideo
./mvnw spring-boot:run
```

3. **Flutter版本启动**
```bash
cd source/client/flutter_cili
flutter pub get
flutter run
```

4. **Android Native版本启动**
```bash
cd source/client/android_cili
./gradlew assembleDebug
```

## 📁 项目结构

```
CiliCili/
├── source/
│   ├── client/
│   │   ├── flutter_cili/     # Flutter版本
│   │   └── android_cili/     # Android Native版本
│   └── server/
│       └── funvideo/         # Spring Boot后端
├── art/                      # 项目资源文件
└── README.md
```

## 📸 项目效果图

### 主要界面
![首页](https://raw.githubusercontent.com/VincentTung/CiliCili/main/art/WechatIMG76.jpeg)
![排行](https://raw.githubusercontent.com/VincentTung/CiliCili/main/art/WechatIMG74.jpeg)
![收藏](https://raw.githubusercontent.com/VincentTung/CiliCili/main/art/WechatIMG73.jpeg)

### 功能界面
![我的](https://raw.githubusercontent.com/VincentTung/CiliCili/main/art/WechatIMG77.jpeg)
![详情页](https://raw.githubusercontent.com/VincentTung/CiliCili/main/art/detail.png)
![模式设置](https://raw.githubusercontent.com/VincentTung/CiliCili/main/art/theme_set.png)
![播放设置](https://raw.githubusercontent.com/VincentTung/CiliCili/main/art/play_set.png)

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🙏 致谢

- **图标设计**: 感谢 [Morgan](https://github.com/Morgan) 提供的精美图标设计

---

⭐ 如果这个项目对您有帮助，请给我一个星标！



