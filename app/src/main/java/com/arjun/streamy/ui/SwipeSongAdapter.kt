package com.arjun.streamy.ui

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.arjun.streamy.R
import com.arjun.streamy.base.BaseSongAdapter
import com.arjun.streamy.data.entities.Song
import kotlinx.android.synthetic.main.swipe_item.view.*
import javax.inject.Inject

class SwipeSongAdapter @Inject constructor() : BaseSongAdapter(R.layout.swipe_item) {

    override val differ: AsyncListDiffer<Song> = AsyncListDiffer(this, itemCallback)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = songs[position]
        when (holder) {
            is SongViewHolder -> {
                holder.itemView.apply {
                    tvPrimary.text = "${item.title} - ${item.artist}"

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

