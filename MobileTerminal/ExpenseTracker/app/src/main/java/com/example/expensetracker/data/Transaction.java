package com.example.expensetracker.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 账单实体类 — Room数据库表
 *
 * 对应论文要求：使用数据库进行数据持久化存储
 * 使用 Room ORM 框架，底层基于 SQLite
 *
 * 字段说明：
 * - id:        自增主键
 * - type:      类型（0=支出, 1=收入）
 * - amount:    金额
 * - category:  消费/收入分类
 * - note:      备注说明
 * - date:      日期（毫秒时间戳）
 */
@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private long id;

    /** 类型：0-支出，1-收入 */
    private int type;

    /** 金额 */
    private double amount;

    /** 分类标签 */
    private String category;

    /** 备注 */
    private String note;

    /** 日期时间戳（毫秒） */
    private long date;

    // ==================== 构造函数 ====================

    public Transaction(int type, double amount, String category, String note, long date) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.date = date;
    }

    // ==================== Getter & Setter ====================

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
