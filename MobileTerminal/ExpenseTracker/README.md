# ExpenseTracker - 个人记账本

一款基于 **Android + Java** 的离线个人财务管理应用，采用 **MVVM 架构**，使用 **Room 数据库** 进行本地持久化存储。支持记录收入与支出、分类筛选、月度汇总等核心记账功能。

---

## 功能特性

### 📊 核心功能

- **收支记录**：支持添加收入（工资等）和支出（餐饮、交通等）记录
- **分类管理**：内置 7 种类别 — 餐饮、交通、购物、娱乐、住房、工资、其他
- **月度汇总**：首页展示当月总支出（红色）、总收入（绿色）及结余
- **筛选过滤**：支持按"全部 / 支出 / 收入 / 餐饮 / 交通 / 购物"快速筛选
- **编辑删除**：点击记录进入编辑模式，长按弹出删除确认对话框
- **日期选择**：支持为每笔记录选择具体日期

### 🎨 界面设计

- Material Design 3 设计语言
- 卡片式布局，视觉层次清晰
- 收入/支出用绿色/红色区分
- 金额格式化为人民币（¥）显示
- 自适应图标（Adaptive Icon）

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 语言 | Java 17 |
| 架构 | MVVM（Model-View-ViewModel） |
| 数据库 | Room 2.6.1（SQLite ORM） |
| UI 框架 | Material Design 3 + RecyclerView + CardView |
| 数据绑定 | ViewBinding |
| 响应式 | LiveData |
| 构建工具 | Gradle 8.7 + AGP 8.5.0 |

### 依赖库

```kotlin
// app/build.gradle.kts
implementation("androidx.room:room-runtime:2.6.1")
annotationProcessor("androidx.room:room-compiler:2.6.1")
implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.0")
implementation("androidx.lifecycle:lifecycle-livedata:2.8.0")
implementation("com.google.android.material:material:1.11.0")
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("androidx.cardview:cardview:1.0.0")
```

---

## 项目结构

```
ExpenseTracker/
├── build.gradle.kts                 # 根构建文件
├── settings.gradle.kts              # 项目设置
├── gradle.properties                # Gradle 配置
├── app/
│   ├── build.gradle.kts             # App 模块构建配置
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/expensetracker/
│       │   ├── data/
│       │   │   ├── Transaction.java          # Room 实体类
│       │   │   ├── TransactionDao.java       # 数据访问对象（DAO）
│       │   │   └── AppDatabase.java          # Room 数据库单例
│       │   ├── viewmodel/
│       │   │   └── ExpenseViewModel.java     # ViewModel 层
│       │   └── ui/
│       │       ├── MainActivity.java         # 主页面
│       │       ├── AddExpenseActivity.java   # 添加/编辑页面
│       │       └── ExpenseAdapter.java       # RecyclerView 适配器
│       └── res/
│           ├── drawable/           # 图标（添加、返回、保存）
│           ├── layout/             # XML 布局文件
│           ├── menu/               # 菜单定义
│           └── values/             # 颜色、字符串、主题
```

---

## 数据模型

### Transaction（交易记录）

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long (PK) | 自增主键 |
| `type` | int | 0 = 支出，1 = 收入 |
| `amount` | double | 金额 |
| `category` | String | 类别标签 |
| `note` | String | 备注 |
| `date` | long | 时间戳（毫秒） |

---

## 环境要求

| 配置项 | 要求 |
|--------|------|
| Android SDK | API 35（compileSdk/targetSdk） |
| 最低系统版本 | Android 8.0（API 26） |
| JDK | JDK 17 |
| Gradle | 8.7（Wrapper 自动下载） |
| Android Studio | 建议最新稳定版 |

---

## 安装与运行

### 1. 克隆项目

```bash
git clone https://github.com/lifeng-z/yuan.git
cd yuan/MobileTerminal/ExpenseTracker
```

### 2. 用 Android Studio 打开

1. 启动 Android Studio
2. 点击 **File → Open**，选择 `ExpenseTracker` 目录
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
- 首次运行会创建本地数据库，数据库名称为 `expense_tracker_db`

---

## 使用指南

### 添加记录

1. 打开 App，点击右下角 **+** 按钮
2. 选择交易类型：**支出**（默认，红色）或 **收入**（绿色）
3. 输入金额
4. 选择分类（餐饮 / 交通 / 购物 / 娱乐 / 住房 / 工资 / 其他）
5. 点击 **日期** 按钮选择日期
6. （可选）填写备注信息
7. 点击工具栏右侧 **保存** 图标 💾

### 查看与筛选

- **月度汇总**：首页顶部卡片显示当月总支出、总收入和结余
- **分类筛选**：点击顶部横向滚动的标签（全部 / 支出 / 收入 / 餐饮 / 交通 / 购物）
- **菜单筛选**：点击右上角溢出菜单，选择"全部 / 只显示支出 / 只显示收入"

### 编辑记录

- 点击列表中的任意记录进入编辑页面
- 修改内容后点击保存即可更新

### 删除记录

- 长按列表中的记录
- 在弹出的确认对话框中点击"确定"

---

## 架构说明

本项目采用 **MVVM（Model-View-ViewModel）** 架构：

```
┌──────────────────────────────────────────┐
│                  View                     │
│  MainActivity / AddExpenseActivity        │
│  (观察 LiveData，响应 UI 事件)              │
└──────────────┬───────────────────────────┘
               │ 观察 / 调用
┌──────────────▼───────────────────────────┐
│              ViewModel                    │
│  ExpenseViewModel                         │
│  (持有 LiveData，封装业务逻辑)               │
└──────────────┬───────────────────────────┘
               │ 访问
┌──────────────▼───────────────────────────┐
│               Model                       │
│  Transaction / TransactionDao / Database  │
│  (Room 实体、DAO、数据库)                    │
└──────────────────────────────────────────┘
```

- **单向数据流**：View 通过 ViewModel 获取 LiveData，数据变更自动刷新 UI
- **后台写入**：所有增删改操作在后台线程池执行，不阻塞主线程
- **数据库单例**：使用双重校验锁（DCL）确保 Room Database 全局唯一

---

## 许可证

本项目为东北大学秦皇岛分校"移动终端程序设计"课程结课项目，仅供学习参考。
