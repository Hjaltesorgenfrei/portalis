package com.hjadal.portalis

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hjadal.portalis.databinding.ItemChaptersviewBinding

class ChapterAdapter(private val chapters: List<Chapter>, private val onClick: (Chapter) -> Unit) :
    RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemChaptersviewBinding, onClick: (Chapter) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        private val title: TextView = binding.title
        private val chapterNumber: TextView = binding.chapterNumber
        private var currentChapter: Chapter? = null

        init {
            binding.root.setOnClickListener {
                currentChapter?.let {
                    onClick(it)
                }
            }
        }

        fun bind(chapter: Chapter) {
            currentChapter = chapter
            title.text = chapter.title
            chapterNumber.text = chapter.number
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val binding = ItemChaptersviewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chapters[position])
    }

    override fun getItemCount(): Int {
        return this.chapters.size
    }
}