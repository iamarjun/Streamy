package com.arjun.streamy.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.arjun.streamy.R
import com.arjun.streamy.data.entities.Song
import com.arjun.streamy.exoplayer.toSong
import com.arjun.streamy.ui.MainViewModel
import com.arjun.streamy.util.Resource
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    internal lateinit var glide: RequestManager

    private val songViewModel by viewModels<SongViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    private var currentPlayingSong: Song? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            mainViewModel.mediaItems.collect {
                when (it) {
                    is Resource.Success -> {

                        if (currentPlayingSong == null && it.data.isNotEmpty()) {
                            currentPlayingSong = it.data[0]
                            updateTitleAndSongImage(it.data[0])
                        }
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            mainViewModel.currentPlayingSong.collect {
                if (it == null) return@collect

                currentPlayingSong = it.toSong()
                glide.load(currentPlayingSong?.albumArt).into(ivSongImage)
                updateTitleAndSongImage(currentPlayingSong!!)
            }
        }
        lifecycleScope.launchWhenStarted { }
        lifecycleScope.launchWhenStarted { }


    }

    private fun updateTitleAndSongImage(song: Song) {
        val title = "${song.title} - ${song.artist}"
        tvSongName.text = title
        glide.load(song.albumArt).into(ivSongImage)
    }

}