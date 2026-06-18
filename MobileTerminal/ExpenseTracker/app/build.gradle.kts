// App模块构建配置 — 个人记账本 (Expense Tracker) — Java版本
// 移动终端程序设计结课论文项目
plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.expensetracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.expensetracker"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // 启用ViewBinding
    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // AndroidX核心库
    implementation("androidx.core:core:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Material Design 3 — 实现美观的UI界面 (对应论文界面要求)
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Room数据库 — 使用annotationProcessor (Java项目)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    // ViewModel和LiveData — MVVM架构
    val lifecycleVersion = "2.8.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime:$lifecycleVersion")
    // ViewModel的Java注解处理器
    annotationProcessor("androidx.lifecycle:lifecycle-compiler:$lifecycleVersion")

    // RecyclerView — 列表展示账单
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // CardView — 卡片式布局
    implementation("androidx.cardview:cardview:1.0.0")

    // Activity KTX (for Java, use activity:activity)
    implementation("androidx.activity:activity:1.9.0")
    implementation("androidx.fragment:fragment:1.7.1")

    // 测试依赖
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
