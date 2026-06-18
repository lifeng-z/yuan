package com.example.expensetracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker.R;
import com.example.expensetracker.data.Transaction;
import com.example.expensetracker.databinding.ActivityMainBinding;
import com.example.expensetracker.viewmodel.ExpenseViewModel;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * 主活动 — 账单列表 + 收支统计页面 (Java版)
 *
 * 核心功能：
 * 1. 展示所有账单记录
 * 2. 顶部显示本月收支汇总卡片
 * 3. FAB按钮添加新账单
 * 4. 点击编辑，长按删除
 * 5. 筛选功能（全部/支出/收入/按分类）
 *
 * 对应论文功能要求：基本功能实现，功能完善
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ExpenseViewModel viewModel;
    private ExpenseAdapter adapter;

    /** 编辑账单的ActivityResultLauncher */
    private final ActivityResultLauncher<Intent> editLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> { /* 数据通过LiveData自动刷新 */ }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // 设置Toolbar
        setSupportActionBar(binding.toolbar);

        // 初始化列表
        setupRecyclerView();

        // 设置FAB — 添加新账单
        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            editLauncher.launch(intent);
        });

        // 观察数据变化
        observeData();

        // 设置筛选Tab
        setupFilterChips();
    }

    /**
     * 初始化RecyclerView和Adapter
     */
    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(
                transaction -> {
                    // 点击 → 编辑
                    Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                    intent.putExtra(AddExpenseActivity.EXTRA_TRANSACTION_ID, transaction.getId());
                    editLauncher.launch(intent);
                },
                transaction -> {
                    // 长按 → 删除确认
                    showDeleteDialog(transaction);
                }
        );

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    /**
     * 观察LiveData数据变化
     */
    private void observeData() {
        // 观察账单列表
        viewModel.getAllTransactions().observe(this, transactions -> {
            adapter.submitList(transactions);
            updateEmptyState(transactions == null || transactions.isEmpty());
        });

        // 观察总支出
        viewModel.getTotalExpense().observe(this, expense -> {
            NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.CHINA);
            binding.textTotalExpense.setText(fmt.format(expense != null ? expense : 0));
        });

        // 观察总收入
        viewModel.getTotalIncome().observe(this, income -> {
            NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.CHINA);
            binding.textTotalIncome.setText(fmt.format(income != null ? income : 0));
        });

        // 计算结余
        viewModel.getTotalIncome().observe(this, income -> {
            Double expense = viewModel.getTotalExpense().getValue();
            double balance = (income != null ? income : 0) - (expense != null ? expense : 0);
            NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.CHINA);
            binding.textBalance.setText(fmt.format(balance));
        });

        viewModel.getTotalExpense().observe(this, expense -> {
            Double income = viewModel.getTotalIncome().getValue();
            double balance = (income != null ? income : 0) - (expense != null ? expense : 0);
            NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.CHINA);
            binding.textBalance.setText(fmt.format(balance));
        });
    }

    /**
     * 设置筛选Chip点击事件
     */
    private void setupFilterChips() {
        binding.chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.getAllTransactions().observe(this, transactions ->
                        adapter.submitList(transactions));
            }
        });

        binding.chipExpense.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.getExpenses().observe(this, transactions ->
                        adapter.submitList(transactions));
            }
        });

        binding.chipIncome.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.getIncomes().observe(this, transactions ->
                        adapter.submitList(transactions));
            }
        });

        binding.chipFood.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.getByCategory("餐饮").observe(this, transactions ->
                        adapter.submitList(transactions));
            }
        });

        binding.chipTransport.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.getByCategory("交通").observe(this, transactions ->
                        adapter.submitList(transactions));
            }
        });

        binding.chipShopping.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.getByCategory("购物").observe(this, transactions ->
                        adapter.submitList(transactions));
            }
        });
    }

    /**
     * 更新空状态提示
     */
    private void updateEmptyState(boolean isEmpty) {
        binding.textEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    /**
     * 显示删除确认对话框
     */
    private void showDeleteDialog(Transaction transaction) {
        String title = transaction.getType() == 1 ? "删除收入记录？" : "删除支出记录？";
        String typeStr = transaction.getType() == 1 ? "收入" : "支出";
        String message = "确定要删除这笔" + typeStr + "记录吗？\n"
                + "金额：¥" + String.format("%.2f", transaction.getAmount()) + "\n"
                + "此操作不可撤销。";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("删除", (dialog, which) -> viewModel.delete(transaction))
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter_all) {
            viewModel.getAllTransactions().observe(this, transactions ->
                    adapter.submitList(transactions));
            return true;
        } else if (id == R.id.action_filter_expense) {
            viewModel.getExpenses().observe(this, transactions ->
                    adapter.submitList(transactions));
            return true;
        } else if (id == R.id.action_filter_income) {
            viewModel.getIncomes().observe(this, transactions ->
                    adapter.submitList(transactions));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
