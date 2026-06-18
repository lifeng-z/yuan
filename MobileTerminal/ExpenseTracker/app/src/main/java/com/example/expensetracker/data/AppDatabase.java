package com.example.expensetracker.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room数据库类 — 应用的持久化存储核心
 *
 * 使用单例模式确保全局只有一个数据库实例
 * 提供后台线程池用于异步数据库写操作
 *
 * 对应论文要求：数据的持久化保存方式——使用数据库
 */
@Database(entities = {Transaction.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    /** 获取TransactionDao实例 */
    public abstract TransactionDao transactionDao();

    /** 数据库名称 */
    private static final String DATABASE_NAME = "expense_tracker_db";

    /** 后台线程池，用于数据库写操作（避免阻塞主线程） */
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /** 单例实例，使用volatile确保多线程可见性 */
    private static volatile AppDatabase INSTANCE;

    /**
     * 获取数据库实例（双重检查锁定单例模式）
     *
     * @param context 应用上下文
     * @return AppDatabase单例
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
