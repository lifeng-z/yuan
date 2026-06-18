package com.example.smartmemo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room数据库类 — 应用的持久化存储核心
 *
 * 使用单例模式确保整个应用只有一个数据库实例
 * 对应论文要求1.2第3条：数据的持久化保存方式——使用数据库
 *
 * Room是Google推荐的SQLite抽象层，提供：
 * 1. 编译时SQL校验
 * 2. 自动生成CRUD代码
 * 3. 与LiveData/Flow无缝集成
 */
@Database(
    entities = [Note::class],  // 注册实体类
    version = 1,               // 数据库版本号
    exportSchema = false       // 不导出schema（生产环境建议开启）
)
abstract class NoteDatabase : RoomDatabase() {

    /** 获取NoteDao实例 */
    abstract fun noteDao(): NoteDao

    companion object {
        /** 数据库名称 */
        private const val DATABASE_NAME = "smart_memo_db"

        /** 单例实例，使用@Volatile确保多线程可见性 */
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        /**
         * 获取数据库实例（线程安全的双重检查锁定单例模式）
         *
         * @param context 应用上下文
         * @return NoteDatabase单例实例
         */
        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    DATABASE_NAME
                )
                    // 数据库损坏时的重建策略（生产环境建议添加migration）
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
