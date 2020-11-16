package com.arjun.streamy.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arjun.streamy.data.entities.Song
import com.arjun.streamy.databinding.ListItemBinding
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClickListener: ((Song) -> Unit)? = null

    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    fun setOnItemClickListener(listener: ((Song) -> Unit)) {
        onItemClickListener = listener
    }

    private val itemCallback = object : DiffUtil.ItemCallback<Song>() {

        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    private val differ = AsyncListDiffer(this, itemCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return SongViewHolder(
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SongViewHolder -> {
                holder.bind(differ.currentList[position], glide)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class SongViewHolder
    constructor(
        private val binding: ListItemBinding,
        private val listener: ((Song) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Song, glide: RequestManager) {

            binding.apply {
                tvPrimary.text = item.title
                tvSecondary.text = item.artist
                glide.load(item.albumArt).into(ivItemImage)

                root.setOnClickListener {
                    listener?.let {
                        it(item)
                    }
                }
            }
        }
    }
}

