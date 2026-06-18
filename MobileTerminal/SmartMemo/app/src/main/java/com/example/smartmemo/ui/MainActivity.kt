package com.example.smartmemo.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.smartmemo.R
import com.example.smartmemo.data.Note
import com.example.smartmemo.databinding.ActivityMainBinding
import com.example.smartmemo.viewmodel.NoteViewModel

/**
 * 主活动 — 笔记列表页面
 *
 * 核心功能：
 * 1. 瀑布流展示所有笔记
 * 2. 支持搜索和分类筛选
 * 3. 点击笔记进入编辑，长按删除
 * 4. FAB按钮创建新笔记
 *
 * 对应论文功能要求：基本功能实现，功能完善
 */
class MainActivity : AppCompatActivity() {

    /** ViewBinding实例 */
    private lateinit var binding: ActivityMainBinding

    /** ViewModel实例 */
    private lateinit var noteViewModel: NoteViewModel

    /** 笔记适配器 */
    private lateinit var adapter: NoteAdapter

    /** 当前是否为网格布局 */
    private var isGridLayout = true

    /**
     * 编辑笔记的ActivityResultLauncher
     * 编辑完成后无需处理返回结果（数据通过LiveData自动刷新）
     */
    private val editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* 数据通过LiveData自动刷新，无需手动处理 */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化ViewModel
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        // 设置Toolbar
        setSupportActionBar(binding.toolbar)

        // 初始化RecyclerView和Adapter
        setupRecyclerView()

        // 设置FAB点击事件 — 创建新笔记
        binding.fabAddNote.setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            editLauncher.launch(intent)
        }

        // 观察数据变化，自动更新列表
        noteViewModel.allNotes.observe(this) { notes ->
            adapter.submitList(notes)
            // 更新空状态提示
            updateEmptyState(notes.isEmpty())
        }
    }

    /**
     * 设置RecyclerView和相关配置
     */
    private fun setupRecyclerView() {
        adapter = NoteAdapter(
            onItemClick = { note ->
                // 点击笔记 → 进入编辑模式
                val intent = Intent(this, EditNoteActivity::class.java).apply {
                    putExtra(EditNoteActivity.EXTRA_NOTE_ID, note.id)
                }
                editLauncher.launch(intent)
            },
            onItemLongClick = { note ->
                // 长按笔记 → 弹出删除确认对话框
                showDeleteDialog(note)
            }
        )

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@MainActivity.adapter

            // 添加滚动动画
            setHasFixedSize(false)
        }

        // 下拉刷新（重新加载数据）
        binding.swipeRefresh.setOnRefreshListener {
            // LiveData会自动刷新，所以只需停止刷新动画
            binding.swipeRefresh.isRefreshing = false
        }
    }

    /**
     * 更新空状态提示
     * 当没有笔记时显示引导提示
     */
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.textEmpty.let {
            if (isEmpty) {
                it.setText(R.string.empty_hint)
            }
            // 使用alpha控制可见性以实现动画效果
            it.alpha = if (isEmpty) 1.0f else 0.0f
        }
    }

    /**
     * 显示删除确认对话框
     * 防止误删操作
     */
    private fun showDeleteDialog(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("确认删除")
            .setMessage("确定要删除笔记「${note.title}」吗？此操作不可撤销。")
            .setPositiveButton("删除") { _, _ ->
                noteViewModel.delete(note)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    /**
     * 创建选项菜单（搜索和筛选）
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // 设置搜索视图
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "搜索笔记..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 提交搜索时，根据关键词过滤
                if (!query.isNullOrBlank()) {
                    noteViewModel.searchNotes(query).observe(this@MainActivity) { notes ->
                        adapter.submitList(notes)
                    }
                } else {
                    // 关键词为空时显示全部
                    noteViewModel.allNotes.observe(this@MainActivity) { notes ->
                        adapter.submitList(notes)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    noteViewModel.searchNotes(newText).observe(this@MainActivity) { notes ->
                        adapter.submitList(notes)
                    }
                } else {
                    noteViewModel.allNotes.observe(this@MainActivity) { notes ->
                        adapter.submitList(notes)
                    }
                }
                return true
            }
        })

        return true
    }

    /**
     * 菜单项点击处理
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter_all -> {
                noteViewModel.allNotes.observe(this) { notes -> adapter.submitList(notes) }
                true
            }
            R.id.action_filter_work -> {
                noteViewModel.getNotesByCategory("工作").observe(this) { notes ->
                    adapter.submitList(notes)
                }
                true
            }
            R.id.action_filter_study -> {
                noteViewModel.getNotesByCategory("学习").observe(this) { notes ->
                    adapter.submitList(notes)
                }
                true
            }
            R.id.action_filter_life -> {
                noteViewModel.getNotesByCategory("生活").observe(this) { notes ->
                    adapter.submitList(notes)
                }
                true
            }
            // 切换布局
            R.id.action_toggle_layout -> {
                isGridLayout = !isGridLayout
                binding.recyclerView.layoutManager = if (isGridLayout) {
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                } else {
                    LinearLayoutManager(this)
                }
                item.setIcon(
                    if (isGridLayout) R.drawable.ic_grid else R.drawable.ic_list
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
