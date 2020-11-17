package com.arjun.streamy.ui.home

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.arjun.streamy.R
import com.arjun.streamy.base.BaseSongAdapter
import com.arjun.streamy.data.entities.Song
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.list_item.view.*
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager,
) : BaseSongAdapter(R.layout.list_item) {

    override val differ: AsyncListDiffer<Song> = AsyncListDiffer(this, itemCallback)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = songs[position]
        when (holder) {
            is SongViewHolder -> {
                holder.itemView.apply {
                    tvPrimary.text = item.title
                    tvSecondary.text = item.artist
                    glide.load(item.albumArt).into(ivItemImage)

                    setOnClickListener {
                        onItemClickListener?.let {
                            it(item)
                        }
                    }
                }
            }
        }
    }
}

