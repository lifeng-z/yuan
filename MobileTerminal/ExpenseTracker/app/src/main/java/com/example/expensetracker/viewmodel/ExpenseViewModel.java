package com.example.expensetracker.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.AppDatabase;
import com.example.expensetracker.data.Transaction;
import com.example.expensetracker.data.TransactionDao;

import java.util.List;

/**
 * 账单ViewModel — MVVM架构的ViewModel层 (Java版)
 *
 * 负责：
 * 1. 持有UI所需的数据状态
 * 2. 封装数据库操作
 * 3. 通过LiveData自动通知View层数据变化
 *
 * 使用 AndroidViewModel 获取 Application 上下文来初始化数据库
 */
public class ExpenseViewModel extends AndroidViewModel {

    /** 数据库访问对象 */
    private final TransactionDao dao;

    /** 所有账单的LiveData（View层观察后自动更新UI） */
    private final LiveData<List<Transaction>> allTransactions;

    /** 总支出和总收入 */
    private final LiveData<Double> totalExpense;
    private final LiveData<Double> totalIncome;

    public ExpenseViewModel(Application application) {
        super(application);

        // 初始化数据库和DAO
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.transactionDao();

        // 获取LiveData
        allTransactions = dao.getAllTransactions();
        totalExpense = dao.getTotalExpense();
        totalIncome = dao.getTotalIncome();
    }

    // ==================== Getter方法 ====================

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<Double> getTotalExpense() {
        return totalExpense;
    }

    public LiveData<Double> getTotalIncome() {
        return totalIncome;
    }

    public LiveData<List<Transaction>> getExpenses() {
        return dao.getExpenses();
    }

    public LiveData<List<Transaction>> getIncomes() {
        return dao.getIncomes();
    }

    public LiveData<List<Transaction>> getByCategory(String category) {
        return dao.getByCategory(category);
    }

    // ==================== 数据库写操作（异步执行） ====================

    /**
     * 插入新账单（在后台线程执行）
     */
    public void insert(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.insert(transaction);
        });
    }

    /**
     * 更新已有账单
     */
    public void update(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.update(transaction);
        });
    }

    /**
     * 删除账单
     */
    public void delete(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.delete(transaction);
        });
    }

    /**
     * 根据ID获取账单（用于编辑页面）
     */
    public Transaction getById(long id) {
        return dao.getById(id);
    }
}
