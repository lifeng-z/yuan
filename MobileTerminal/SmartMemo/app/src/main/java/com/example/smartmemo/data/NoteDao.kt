package com.example.smartmemo.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * 笔记数据访问对象（DAO）— 定义数据库CRUD操作
 *
 * Room在编译时自动生成实现类
 * 所有增删改操作使用Kotlin协程异步执行，避免阻塞主线程
 */
@Dao
interface NoteDao {

    /**
     * 获取所有笔记，按更新时间降序排列
     * 使用LiveData实现数据观察，当数据库变化时自动更新UI
     */
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): LiveData<List<Note>>

    /**
     * 按分类筛选笔记
     * @param category 分类名称
     */
    @Query("SELECT * FROM notes WHERE category = :category ORDER BY updatedAt DESC")
    fun getNotesByCategory(category: String): LiveData<List<Note>>

    /**
     * 搜索笔记（标题或内容包含关键词）
     * @param query 搜索关键词，使用LIKE模糊匹配
     */
    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchNotes(query: String): LiveData<List<Note>>

    /**
     * 插入新笔记
     * @return 新插入笔记的ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    /**
     * 更新已有笔记
     */
    @Update
    suspend fun update(note: Note)

    /**
     * 删除笔记
     */
    @Delete
    suspend fun delete(note: Note)

    /**
     * 根据ID获取单条笔记
     */
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?
}
