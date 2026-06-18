package com.example.smartmemo.ui

import android.app.Application

/**
 * 自定义Application类
 *
 * 应用启动时初始化全局配置
 * 在此处可进行数据库预加载、日志初始化等操作
 */
class SmartMemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 全局初始化逻辑在此添加
        // 例如：数据库预填充、第三方SDK初始化等
    }
}
