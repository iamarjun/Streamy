package com.arjun.streamy.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arjun.streamy.R
import com.arjun.streamy.data.entities.Song
import com.arjun.streamy.exoplayer.toSong
import com.arjun.streamy.util.Resource
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    internal lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    internal lateinit var glide: RequestManager

    private val viewModel by viewModels<MainViewModel>()

    private var currentPlayingSong: Song? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vpSong.adapter = swipeSongAdapter

        lifecycleScope.launchWhenStarted {
            viewModel.mediaItems.collect {
                when (it) {
                    Resource.Loading -> Unit
                    is Resource.Success -> {
                        swipeSongAdapter.songs = it.data
                        if (it.data.isNotEmpty())
                            glide.load((currentPlayingSong ?: it.data[0]).albumArt)
                                .into(ivCurSongImage)

                        switchViewPagerToCurrentSong(currentPlayingSong ?: return@collect)
                    }
                    is Resource.Error -> Unit
                }
            }
        }

        viewModel.currentPlayingSong.observe(this) {
            if (it == null) return@observe

            currentPlayingSong = it.toSong()
            glide.load(currentPlayingSong?.albumArt).into(ivCurSongImage)
            switchViewPagerToCurrentSong(currentPlayingSong ?: return@observe)
        }

    }

    private fun switchViewPagerToCurrentSong(song: Song) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if (newItemIndex != -1) {
            vpSong.currentItem = newItemIndex
            currentPlayingSong = song
        }
    }
}