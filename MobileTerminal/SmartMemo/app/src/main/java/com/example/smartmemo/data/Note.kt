package com.example.smartmemo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 笔记实体类 — Room数据库表
 *
 * 对应论文要求1.2第3条：使用数据库进行数据持久化存储
 * 使用Room ORM框架，底层为SQLite数据库
 *
 * @property id 笔记唯一标识，自动生成主键
 * @property title 笔记标题
 * @property content 笔记内容
 * @property category 笔记分类（工作/学习/生活/其他）
 * @property createdAt 创建时间（毫秒时间戳）
 * @property updatedAt 最后更新时间（毫秒时间戳）
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 笔记标题 */
    val title: String,

    /** 笔记正文内容 */
    val content: String,

    /** 分类标签：工作、学习、生活、其他 */
    val category: String = "其他",

    /** 创建时间戳（毫秒） */
    val createdAt: Long = System.currentTimeMillis(),

    /** 最后更新时间戳（毫秒） */
    val updatedAt: Long = System.currentTimeMillis()
)
