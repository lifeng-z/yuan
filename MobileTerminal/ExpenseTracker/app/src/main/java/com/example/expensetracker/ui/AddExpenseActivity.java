package com.example.expensetracker.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.data.Transaction;
import com.example.expensetracker.databinding.ActivityAddExpenseBinding;
import com.example.expensetracker.viewmodel.ExpenseViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 添加/编辑账单活动 (Java版)
 *
 * 功能：
 * 1. 新建账单（无传入ID时）
 * 2. 编辑已有账单（传入ID时，从数据库加载数据）
 * 3. 选择收支类型、分类、金额、日期、备注
 * 4. 输入校验：金额不能为空且必须大于0
 * 5. 退出提醒：有未保存修改时确认
 */
public class AddExpenseActivity extends AppCompatActivity {

    /** Intent Extra Key：传入账单ID（-1表示新建） */
    public static final String EXTRA_TRANSACTION_ID = "extra_transaction_id";

    private ActivityAddExpenseBinding binding;
    private ExpenseViewModel viewModel;

    /** 当前编辑的账单ID（-1表示新建模式） */
    private long currentId = -1;

    /** 选中的日期时间戳 */
    private long selectedDate = System.currentTimeMillis();

    /** 是否为收入类型（默认false=支出） */
    private boolean isIncome = false;

    /** 数据是否已修改 */
    private boolean isDataChanged = false;

    /** 后台线程池 */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // 设置Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 获取传入的账单ID
        currentId = getIntent().getLongExtra(EXTRA_TRANSACTION_ID, -1);

        if (currentId != -1) {
            // 编辑模式
            setTitle("编辑账单");
            loadTransaction();
        } else {
            // 新建模式
            setTitle("新建账单");
        }

        // 设置日期选择按钮
        updateDateDisplay();
        binding.btnDate.setOnClickListener(v -> showDatePicker());

        // 设置类型切换
        binding.chipExpense.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isIncome = false;
                isDataChanged = true;
            }
        });

        binding.chipIncome.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isIncome = true;
                isDataChanged = true;
            }
        });

        // 监听输入变化
        binding.editAmount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) isDataChanged = true;
        });
        binding.editNote.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) isDataChanged = true;
        });
    }

    /**
     * 加载已有账单数据
     */
    private void loadTransaction() {
        executor.execute(() -> {
            Transaction t = viewModel.getById(currentId);
            if (t != null) {
                runOnUiThread(() -> {
                    // 设置金额
                    binding.editAmount.setText(String.format("%.2f", t.getAmount()));

                    // 设置备注
                    binding.editNote.setText(t.getNote());

                    // 设置日期
                    selectedDate = t.getDate();
                    updateDateDisplay();

                    // 设置类型
                    isIncome = (t.getType() == 1);
                    binding.chipExpense.setChecked(!isIncome);
                    binding.chipIncome.setChecked(isIncome);

                    // 设置分类
                    setCategorySelection(t.getCategory());
                });
            }
        });
    }

    /**
     * 显示日期选择器
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDate);

        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, dayOfMonth);
                    selectedDate = cal.getTimeInMillis();
                    updateDateDisplay();
                    isDataChanged = true;
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    /**
     * 更新日期显示
     */
    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        binding.btnDate.setText(sdf.format(new Date(selectedDate)));
    }

    /**
     * 设置分类选择
     */
    private void setCategorySelection(String category) {
        binding.chipFood.setChecked("餐饮".equals(category));
        binding.chipTransport.setChecked("交通".equals(category));
        binding.chipShopping.setChecked("购物".equals(category));
        binding.chipEntertainment.setChecked("娱乐".equals(category));
        binding.chipHousing.setChecked("住房".equals(category));
        binding.chipSalary.setChecked("工资".equals(category));
        binding.chipOther.setChecked("其他".equals(category));
    }

    /**
     * 获取当前选中的分类
     */
    private String getSelectedCategory() {
        if (binding.chipFood.isChecked()) return "餐饮";
        if (binding.chipTransport.isChecked()) return "交通";
        if (binding.chipShopping.isChecked()) return "购物";
        if (binding.chipEntertainment.isChecked()) return "娱乐";
        if (binding.chipHousing.isChecked()) return "住房";
        if (binding.chipSalary.isChecked()) return "工资";
        return "其他";
    }

    /**
     * 保存账单
     */
    private void saveTransaction() {
        String amountStr = binding.editAmount.getText().toString().trim();

        // 输入校验：金额不能为空
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            binding.editAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "金额格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        // 金额必须大于0
        if (amount <= 0) {
            Toast.makeText(this, "金额必须大于0", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = binding.editNote.getText().toString().trim();
        String category = getSelectedCategory();
        int type = isIncome ? 1 : 0;

        if (currentId != -1) {
            // 更新
            Transaction t = new Transaction(type, amount, category, note, selectedDate);
            t.setId(currentId);
            viewModel.update(t);
            Toast.makeText(this, "账单已更新", Toast.LENGTH_SHORT).show();
        } else {
            // 新建
            Transaction t = new Transaction(type, amount, category, note, selectedDate);
            viewModel.insert(t);
            Toast.makeText(this, "账单已添加", Toast.LENGTH_SHORT).show();
        }

        isDataChanged = false;
        finish();
    }

    /**
     * 退出确认
     */
    private void confirmExit() {
        if (isDataChanged) {
            new AlertDialog.Builder(this)
                    .setTitle("放弃修改？")
                    .setMessage("当前账单有未保存的修改，确定要退出吗？")
                    .setPositiveButton("放弃", (dialog, which) -> finish())
                    .setNegativeButton("继续编辑", null)
                    .show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            saveTransaction();
            return true;
        } else if (id == android.R.id.home) {
            confirmExit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        confirmExit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
