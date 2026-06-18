package com.example.expensetracker.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.data.Transaction;
import com.example.expensetracker.databinding.ItemTransactionBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 账单列表适配器 — RecyclerView适配器 (Java版)
 *
 * 使用ListAdapter + DiffUtil实现高效列表更新
 * 点击进入编辑，长按删除
 */
public class ExpenseAdapter extends ListAdapter<Transaction, ExpenseAdapter.ViewHolder> {

    /** 日期格式化 */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    /** 点击回调 */
    private final OnItemClickListener onItemClick;
    /** 长按回调 */
    private final OnItemLongClickListener onItemLongClick;

    // ==================== 回调接口 ====================

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Transaction transaction);
    }

    // ==================== 构造函数 ====================

    public ExpenseAdapter(OnItemClickListener clickListener,
                          OnItemLongClickListener longClickListener) {
        super(new TransactionDiffCallback());
        this.onItemClick = clickListener;
        this.onItemLongClick = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    // ==================== ViewHolder ====================

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemTransactionBinding binding;

        ViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Transaction transaction) {
            // 绑定金额（收入显示"+", 支出显示"-"）
            String amountStr;
            if (transaction.getType() == 1) {
                // 收入 — 绿色
                amountStr = "+¥" + String.format("%.2f", transaction.getAmount());
                binding.textAmount.setTextColor(
                        binding.getRoot().getContext().getColor(android.R.color.holo_green_dark));
            } else {
                // 支出 — 红色
                amountStr = "-¥" + String.format("%.2f", transaction.getAmount());
                binding.textAmount.setTextColor(
                        binding.getRoot().getContext().getColor(android.R.color.holo_red_dark));
            }
            binding.textAmount.setText(amountStr);

            // 绑定分类
            binding.textCategory.setText(transaction.getCategory());

            // 绑定备注
            binding.textNote.setText(transaction.getNote());

            // 绑定日期
            binding.textDate.setText(dateFormat.format(new Date(transaction.getDate())));

            // 设置类型标签颜色
            if (transaction.getType() == 1) {
                binding.chipType.setText("收入");
                binding.chipType.setChipBackgroundColorResource(android.R.color.holo_green_light);
            } else {
                binding.chipType.setText("支出");
                binding.chipType.setChipBackgroundColorResource(android.R.color.holo_red_light);
            }

            // 点击事件
            binding.getRoot().setOnClickListener(v -> onItemClick.onItemClick(transaction));

            // 长按事件
            binding.getRoot().setOnLongClickListener(v -> {
                onItemLongClick.onItemLongClick(transaction);
                return true;
            });
        }
    }

    // ==================== DiffUtil ====================

    static class TransactionDiffCallback extends DiffUtil.ItemCallback<Transaction> {
        @Override
        public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.getId() == newItem.getId()
                    && oldItem.getAmount() == newItem.getAmount()
                    && oldItem.getType() == newItem.getType()
                    && oldItem.getCategory().equals(newItem.getCategory());
        }
    }
}
