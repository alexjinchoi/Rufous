package org.antilibrary.rufous

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import org.antilibrary.rufous.bookmark.Bookmark
import org.antilibrary.rufous.databinding.ActivityBookmarkBinding
import org.antilibrary.rufous.databinding.ActivityBookmarkItemBinding

class ListAdapter (private val context: Context, private val BookmarkList: MutableList<Bookmark>) : BaseAdapter() {
    override fun getCount(): Int = BookmarkList.size

    override fun getItem(position: Int): Bookmark = BookmarkList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ActivityBookmarkItemBinding.inflate(LayoutInflater.from(context))

        val show = BookmarkList[position]
        binding.txtSiteName.text = show.siteName
        binding.txtSiteUrl.text = show.siteUrl

        return binding.root

    }

}