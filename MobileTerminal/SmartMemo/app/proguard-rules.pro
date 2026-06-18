# ProGuard混淆规则 — 智能备忘录
# Room数据库相关：保持实体类不被混淆
-keep class com.example.smartmemo.data.** { *; }
# 保持Kotlin数据类
-keepattributes *Annotation*
-keepattributes Signature
