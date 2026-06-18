package com.example.smartmemo.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.smartmemo.data.Note
import com.example.smartmemo.data.NoteDatabase
import kotlinx.coroutines.launch

/**
 * 笔记ViewModel — MVVM架构的ViewModel层
 *
 * 负责：
 * 1. 管理UI相关数据
 * 2. 处理用户交互逻辑
 * 3. 协调数据库操作（通过Repository模式简化）
 *
 * 使用AndroidViewModel以获取Application上下文来初始化数据库
 */
class NoteViewModel(application: Application) : AndroidViewModel(application) {

    /** 数据库访问对象 */
    private val noteDao = NoteDatabase.getDatabase(application).noteDao()

    /** 所有笔记列表（LiveData自动更新UI） */
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    /** 当前搜索关键词 */
    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    /** 当前分类筛选（空字符串表示全部） */
    private val _categoryFilter = MutableLiveData<String>("")
    val categoryFilter: LiveData<String> = _categoryFilter

    /**
     * 插入新笔记
     * 在协程中异步执行，不阻塞UI线程
     */
    fun insert(title: String, content: String, category: String) {
        viewModelScope.launch {
            val note = Note(
                title = title,
                content = content,
                category = category
            )
            noteDao.insert(note)
        }
    }

    /**
     * 更新已有笔记
     * 更新时会自动修改updatedAt时间戳
     */
    fun update(id: Long, title: String, content: String, category: String) {
        viewModelScope.launch {
            val note = Note(
                id = id,
                title = title,
                content = content,
                category = category,
                updatedAt = System.currentTimeMillis()
            )
            noteDao.update(note)
        }
    }

    /**
     * 删除笔记
     */
    fun delete(note: Note) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }

    /**
     * 根据ID获取笔记（用于编辑页面加载数据）
     * @return Note对象，若不存在则返回null
     */
    suspend fun getNoteById(id: Long): Note? {
        return noteDao.getNoteById(id)
    }

    /**
     * 设置搜索关键词
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * 设置分类筛选
     */
    fun setCategoryFilter(category: String) {
        _categoryFilter.value = category
    }

    /**
     * 搜索笔记
     */
    fun searchNotes(query: String): LiveData<List<Note>> {
        return noteDao.searchNotes(query)
    }

    /**
     * 按分类获取笔记
     */
    fun getNotesByCategory(category: String): LiveData<List<Note>> {
        return noteDao.getNotesByCategory(category)
    }
}
