package com.example.expensetracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * 账单数据访问对象（DAO）— 定义数据库CRUD操作
 *
 * Room在编译时自动生成实现类
 * 查询方法返回LiveData以实现响应式UI更新
 */
@Dao
public interface TransactionDao {

    /**
     * 获取所有账单，按日期降序排列
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();

    /**
     * 获取所有支出记录
     */
    @Query("SELECT * FROM transactions WHERE type = 0 ORDER BY date DESC")
    LiveData<List<Transaction>> getExpenses();

    /**
     * 获取所有收入记录
     */
    @Query("SELECT * FROM transactions WHERE type = 1 ORDER BY date DESC")
    LiveData<List<Transaction>> getIncomes();

    /**
     * 按分类筛选账单
     */
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    LiveData<List<Transaction>> getByCategory(String category);

    /**
     * 计算总支出金额
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 0")
    LiveData<Double> getTotalExpense();

    /**
     * 计算总收入金额
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 1")
    LiveData<Double> getTotalIncome();

    /**
     * 按月份查询账单
     */
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startOfMonth AND :endOfMonth ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsByMonth(long startOfMonth, long endOfMonth);

    /**
     * 插入新账单
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Transaction transaction);

    /**
     * 更新账单
     */
    @Update
    void update(Transaction transaction);

    /**
     * 删除账单
     */
    @Delete
    void delete(Transaction transaction);

    /**
     * 根据ID获取单条账单
     */
    @Query("SELECT * FROM transactions WHERE id = :id")
    Transaction getById(long id);
}
