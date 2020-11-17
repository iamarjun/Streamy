package com.arjun.streamy.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arjun.streamy.data.entities.Song

abstract class BaseSongAdapter(private val redId: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: ((Song) -> Unit)? = null

    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    fun setItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }

    protected val itemCallback = object : DiffUtil.ItemCallback<Song>() {

        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    protected abstract val differ: AsyncListDiffer<Song>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(redId, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    protected inner class SongViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)
}

