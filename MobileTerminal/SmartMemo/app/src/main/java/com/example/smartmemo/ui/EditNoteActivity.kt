package com.example.smartmemo.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.smartmemo.R
import com.example.smartmemo.databinding.ActivityEditNoteBinding
import com.example.smartmemo.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

/**
 * 编辑笔记活动 — 新建/编辑笔记页面
 *
 * 功能：
 * 1. 新建笔记（无传入ID时）
 * 2. 编辑已有笔记（有传入ID时，从数据库加载数据）
 * 3. 选择笔记分类
 * 4. 保存/取消操作
 * 5. 未保存修改时的退出提醒
 */
class EditNoteActivity : AppCompatActivity() {

    companion object {
        /** Intent Extra Key：传入笔记ID（null表示新建） */
        const val EXTRA_NOTE_ID = "extra_note_id"
    }

    /** ViewBinding实例 */
    private lateinit var binding: ActivityEditNoteBinding

    /** ViewModel实例 */
    private lateinit var noteViewModel: NoteViewModel

    /** 当前编辑的笔记ID（0表示新建） */
    private var currentNoteId: Long = 0

    /** 跟踪数据是否已修改（用于退出提醒） */
    private var isDataChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化ViewBinding
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化ViewModel
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        // 设置Toolbar和返回按钮
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 获取传入的笔记ID
        currentNoteId = intent.getLongExtra(EXTRA_NOTE_ID, 0)

        if (currentNoteId != 0L) {
            // 编辑模式：加载已有笔记数据
            loadNote(currentNoteId)
            supportActionBar?.title = "编辑笔记"
        } else {
            // 新建模式
            supportActionBar?.title = "新建笔记"
        }

        // 监听文本变化，标记数据已修改
        setupTextChangeListeners()
    }

    /**
     * 从数据库加载笔记数据到编辑界面
     */
    private fun loadNote(noteId: Long) {
        lifecycleScope.launch {
            val note = noteViewModel.getNoteById(noteId)
            note?.let {
                binding.apply {
                    editTitle.setText(it.title)
                    editContent.setText(it.content)
                    // 设置分类选择
                    setCategorySelection(it.category)
                }
            }
        }
    }

    /**
     * 设置分类Chip选择状态
     */
    private fun setCategorySelection(category: String) {
        binding.apply {
            chipWork.isChecked = category == "工作"
            chipStudy.isChecked = category == "学习"
            chipLife.isChecked = category == "生活"
            chipOther.isChecked = category == "其他"
        }
    }

    /**
     * 获取当前选中的分类
     */
    private fun getSelectedCategory(): String {
        return when {
            binding.chipWork.isChecked -> "工作"
            binding.chipStudy.isChecked -> "学习"
            binding.chipLife.isChecked -> "生活"
            else -> "其他"
        }
    }

    /**
     * 监听输入变化
     */
    private fun setupTextChangeListeners() {
        binding.editTitle.setOnFocusChangeListener { _, _ -> isDataChanged = true }
        binding.editContent.setOnFocusChangeListener { _, _ -> isDataChanged = true }
    }

    /**
     * 保存笔记
     * 根据当前是否为新建模式选择insert或update操作
     */
    private fun saveNote() {
        val title = binding.editTitle.text.toString().trim()
        val content = binding.editContent.text.toString().trim()
        val category = getSelectedCategory()

        // 输入校验：标题不能为空
        if (title.isEmpty()) {
            Toast.makeText(this, "请输入笔记标题", Toast.LENGTH_SHORT).show()
            binding.editTitle.requestFocus()
            return
        }

        // 输入校验：内容不能为空
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入笔记内容", Toast.LENGTH_SHORT).show()
            binding.editContent.requestFocus()
            return
        }

        if (currentNoteId != 0L) {
            // 更新已有笔记
            noteViewModel.update(currentNoteId, title, content, category)
            Toast.makeText(this, "笔记已更新", Toast.LENGTH_SHORT).show()
        } else {
            // 插入新笔记
            noteViewModel.insert(title, content, category)
            Toast.makeText(this, "笔记已创建", Toast.LENGTH_SHORT).show()
        }

        isDataChanged = false
        finish() // 返回列表页
    }

    /**
     * 退出确认：有未保存修改时提醒用户
     */
    private fun confirmExit() {
        if (isDataChanged) {
            AlertDialog.Builder(this)
                .setTitle("放弃修改？")
                .setMessage("当前笔记有未保存的修改，确定要退出吗？")
                .setPositiveButton("放弃") { _, _ -> finish() }
                .setNegativeButton("继续编辑", null)
                .show()
        } else {
            finish()
        }
    }

    /**
     * 创建保存菜单按钮
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    /**
     * 菜单项点击处理
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveNote()
                true
            }
            android.R.id.home -> {
                confirmExit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * 拦截返回键，添加退出确认
     */
    override fun onBackPressed() {
        confirmExit()
    }
}
