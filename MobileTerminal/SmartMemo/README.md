# SmartMemo - 智能备忘录

一款基于 **Android + Kotlin** 的智能备忘录应用，采用 **MVVM 架构**，使用 **Room 数据库** 进行本地持久化存储。支持笔记的增删改查、关键词搜索、分类筛选，并提供瀑布流和列表两种展示模式。

---

## 功能特性

### 📝 核心功能

- **创建笔记**：支持标题、正文内容，可选择类别（工作 / 学习 / 生活 / 其他）
- **搜索笔记**：支持按标题和内容进行关键词实时搜索
- **分类筛选**：从工具栏菜单按类别过滤（全部 / 工作 / 学习 / 生活）
- **双布局模式**：支持 **瀑布流网格**（2 列卡片）和 **线性列表** 两种展示方式，可一键切换
- **编辑笔记**：点击笔记卡片进入编辑模式
- **删除笔记**：长按弹出确认对话框，防止误删
- **未保存提醒**：编辑页面有未保存更改时，返回会弹出丢弃确认提示
- **下拉刷新**：SwipeRefreshLayout 支持下拉刷新

### 🎨 界面设计

- Material Design 3 设计语言
- 卡片式布局，内容预览截断显示
- 类别标签带颜色区分（蓝色=工作、绿色=学习、橙色=生活、灰色=其他）
- 空状态友好提示
- 中文本地化界面

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 语言 | Kotlin 1.9.24 |
| 架构 | MVVM（Model-View-ViewModel） |
| 数据库 | Room 2.6.1（KSP 编译） |
| UI 框架 | Material Design 3 + RecyclerView |
| 数据绑定 | ViewBinding |
| 异步处理 | Kotlin Coroutines + LiveData |
| 构建工具 | Gradle 8.7 + AGP 8.5.0 |

### 依赖库

```kotlin
// app/build.gradle.kts
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
implementation("com.google.android.material:material:1.11.0")
implementation("androidx.recyclerview:recyclerview:1.3.2")
// Kotlin Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
```

---

## 项目结构

```
SmartMemo/
├── build.gradle.kts                 # 根构建文件
├── settings.gradle.kts              # 项目设置
├── gradle.properties                # Gradle 配置
├── app/
│   ├── build.gradle.kts             # App 模块构建配置
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/smartmemo/
│       │   ├── data/
│       │   │   ├── Note.kt                   # Room 实体类
│       │   │   ├── NoteDao.kt                # 数据访问对象（DAO）
│       │   │   └── NoteDatabase.kt           # Room 数据库单例
│       │   ├── viewmodel/
│       │   │   └── NoteViewModel.kt          # ViewModel 层
│       │   └── ui/
│       │       ├── SmartMemoApplication.kt   # Application 类
│       │       ├── MainActivity.kt           # 主页面
│       │       ├── EditNoteActivity.kt       # 编辑页面
│       │       └── NoteAdapter.kt            # RecyclerView 适配器
│       └── res/
│           ├── drawable/           # 图标资源
│           ├── layout/             # XML 布局文件
│           ├── menu/               # 菜单定义
│           └── values/             # 颜色、字符串、主题
```

---

## 数据模型

### Note（笔记）

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long (PK) | 自增主键 |
| `title` | String | 笔记标题（最多 50 字） |
| `content` | String | 笔记正文内容 |
| `category` | String | 类别（默认"其他"） |
| `createdAt` | Long | 创建时间（毫秒时间戳） |
| `updatedAt` | Long | 最后更新时间（毫秒时间戳） |

---

## 环境要求

| 配置项 | 要求 |
|--------|------|
| Android SDK | API 35（compileSdk/targetSdk） |
| 最低系统版本 | Android 8.0（API 26） |
| JDK | JDK 17 |
| Gradle | 8.7（Wrapper 自动下载） |
| Kotlin | 1.9.24 |
| KSP | 1.9.24-1.0.20（Room 注解处理） |
| Android Studio | 建议最新稳定版 |

---

## 安装与运行

### 1. 克隆项目

```bash
git clone https://github.com/lifeng-z/yuan.git
cd yuan/MobileTerminal/SmartMemo
```

### 2. 用 Android Studio 打开

1. 启动 Android Studio
2. 点击 **File → Open**，选择 `SmartMemo` 目录
3. 等待 Gradle 同步完成（会自动下载依赖）

### 3. 构建与安装

**方式一：Android Studio 运行**
- 连接 Android 设备或启动模拟器
- 点击工具栏的 **Run** 按钮（绿色三角形）

**方式二：命令行构建**
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 4. 注意事项

- `local.properties` 中的 `sdk.dir` 需指向你的 Android SDK 路径
- `gradle.properties` 中的 `org.gradle.java.home` 需指向你本地的 JDK 路径
- 首次运行会创建本地数据库，数据库名称为 `notes_db`（含 `notes` 表）

---

## 使用指南

### 创建笔记

1. 打开 App，点击右下角 **+** 按钮
2. 输入 **标题**（必填，最多 50 字）
3. 选择 **类别**：工作 / 学习 / 生活 / 其他（点击标签切换）
4. 输入 **正文内容**（必填，多行文本）
5. 点击工具栏右侧 **保存** 图标 💾

### 查看与切换布局

- **瀑布流模式**（默认）：2 列卡片网格，适合浏览多篇笔记
- **列表模式**：单列线性排列，适合查看详细内容
- 点击工具栏的 **布局切换** 图标（网格 / 列表）可在两种模式间切换

### 搜索笔记

- 点击工具栏的 **搜索** 图标 🔍
- 输入关键词，App 会同时在 **标题** 和 **正文内容** 中匹配
- 搜索结果实时更新

### 分类筛选

- 点击工具栏右上角 **溢出菜单** → **过滤**
- 选择：全部 / 工作 / 学习 / 生活

### 编辑笔记

- 点击任意笔记卡片进入编辑页面
- 修改标题、类别或内容后点击保存

### 删除笔记

- 长按笔记卡片
- 在弹出的确认对话框中点击"确定"

### 未保存提醒

- 在编辑页面修改内容后，按返回键或 Home 键
- App 会弹出提示："放弃更改？" → 选择"放弃"或"继续编辑"

---

## 架构说明

本项目采用 **MVVM（Model-View-ViewModel）** 架构：

```
┌──────────────────────────────────────────┐
│                  View                     │
│  MainActivity / EditNoteActivity          │
│  (观察 LiveData，响应 UI 事件)              │
└──────────────┬───────────────────────────┘
               │ 观察 / 调用
┌──────────────▼───────────────────────────┐
│              ViewModel                    │
│  NoteViewModel                            │
│  (持有 LiveData，管理搜索/筛选状态)          │
│  (使用 viewModelScope 执行协程)             │
└──────────────┬───────────────────────────┘
               │ 访问
┌──────────────▼───────────────────────────┐
│               Model                       │
│  Note / NoteDao / NoteDatabase            │
│  (Room 实体、DAO、数据库)                   │
└──────────────────────────────────────────┘
```

- **协程异步**：所有数据库读写通过 `viewModelScope.launch` 在后台线程执行
- **响应式 UI**：LiveData 驱动列表自动刷新，无需手动刷新
- **KSP 编译**：Room 使用 KSP（Kotlin Symbol Processing）替代传统注解处理器，编译速度更快

---

## 许可证

本项目为东北大学秦皇岛分校"移动终端程序设计"课程结课项目，仅供学习参考。
