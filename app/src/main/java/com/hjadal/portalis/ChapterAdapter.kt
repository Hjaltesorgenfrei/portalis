package com.hjadal.portalis

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.hjadal.portalis.databinding.ItemChaptersviewBinding

class ChapterAdapter(private val context: Context, private val chapters: List<Chapter>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return this.chapters.size
    }

    override fun getItem(position: Int): Chapter {
        return chapters[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val chapter = getItem(position)
        var view: View
        if (convertView == null) {
            view =  ItemChaptersviewBinding.inflate(inflater, parent, false).root
        } else {
            view = convertView
        }
        val binding = (ItemChaptersviewBinding.bind(view))
        binding.textView.text = chapter.title
        return view
    }
}