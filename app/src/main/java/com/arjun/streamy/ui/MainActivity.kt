package com.arjun.streamy.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.arjun.streamy.R
import com.arjun.streamy.data.entities.Song
import com.arjun.streamy.exoplayer.isPlaying
import com.arjun.streamy.exoplayer.toSong
import com.arjun.streamy.util.Resource
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
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
    private var playbackState: PlaybackStateCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vpSong.adapter = swipeSongAdapter

        ivPlayPause.setOnClickListener {
            currentPlayingSong?.let {
                viewModel.playOrToggle(it, true)
            }
        }

        vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (playbackState?.isPlaying == true)
                    viewModel.playOrToggle(swipeSongAdapter.songs[position])
                else
                    currentPlayingSong = swipeSongAdapter.songs[position]
            }
        })

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

        lifecycleScope.launchWhenStarted {
            viewModel.playbackState.collect {
                playbackState = it
                ivPlayPause.setImageResource(
                    if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
                )
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.isConnected.collect {
                when (it) {
                    is Resource.Success -> Unit
                    is Resource.Error -> Snackbar.make(
                        rootLayout,
                        it.exception.localizedMessage ?: "An unknown error occured",
                        Snackbar.LENGTH_LONG
                    ).show()
                    Resource.Loading -> Unit
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.networkError.collect {
                when (it) {
                    is Resource.Success -> Unit
                    is Resource.Error -> Snackbar.make(
                        rootLayout,
                        it.exception.localizedMessage ?: "An unknown error occured",
                        Snackbar.LENGTH_LONG
                    ).show()
                    Resource.Loading -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.currentPlayingSong.collect {
                if (it == null) return@collect

                currentPlayingSong = it.toSong()
                glide.load(currentPlayingSong?.albumArt).into(ivCurSongImage)
                switchViewPagerToCurrentSong(currentPlayingSong ?: return@collect)
            }
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