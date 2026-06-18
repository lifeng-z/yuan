package com.example.smartmemo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartmemo.data.Note
import com.example.smartmemo.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * 笔记列表适配器 — RecyclerView适配器
 *
 * 使用ListAdapter + DiffUtil实现高效列表更新
 * 只有变化的数据项才会刷新，提升列表滚动性能
 */
class NoteAdapter(
    private val onItemClick: (Note) -> Unit,      // 点击回调：编辑笔记
    private val onItemLongClick: (Note) -> Unit    // 长按回调：删除笔记
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    /** 日期格式化工具 */
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder — 绑定单条笔记数据到视图
     */
    inner class NoteViewHolder(
        private val binding: ItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.apply {
                // 绑定标题
                textTitle.text = note.title

                // 绑定内容预览（截取前100字符）
                textContent.text = if (note.content.length > 100) {
                    note.content.substring(0, 100) + "..."
                } else {
                    note.content
                }

                // 绑定分类标签
                chipCategory.text = note.category
                // 根据分类设置不同颜色
                when (note.category) {
                    "工作" -> chipCategory.setChipBackgroundColorResource(android.R.color.holo_blue_light)
                    "学习" -> chipCategory.setChipBackgroundColorResource(android.R.color.holo_green_light)
                    "生活" -> chipCategory.setChipBackgroundColorResource(android.R.color.holo_orange_light)
                    else -> chipCategory.setChipBackgroundColorResource(android.R.color.darker_gray)
                }

                // 绑定更新时间
                textDate.text = dateFormat.format(Date(note.updatedAt))

                // 设置点击事件：点击进入编辑页面
                root.setOnClickListener {
                    onItemClick(note)
                }

                // 设置长按事件：弹出删除确认
                root.setOnLongClickListener {
                    onItemLongClick(note)
                    true
                }
            }
        }
    }

    /**
     * DiffUtil回调 — 高效计算列表差异
     */
    private class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}
