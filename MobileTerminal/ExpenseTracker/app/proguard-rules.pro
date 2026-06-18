# ProGuard规则 — 个人记账本 (Java项目)
# Room数据库实体类保持不被混淆
-keep class com.example.expensetracker.data.** { *; }
-keepattributes *Annotation*
