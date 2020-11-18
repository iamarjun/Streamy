package com.arjun.streamy.ui.detail

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.arjun.streamy.R
import com.arjun.streamy.data.entities.Song
import com.arjun.streamy.exoplayer.isPlaying
import com.arjun.streamy.exoplayer.toSong
import com.arjun.streamy.ui.MainViewModel
import com.arjun.streamy.util.Resource
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_song.*
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    internal lateinit var glide: RequestManager

    private val songViewModel by viewModels<SongViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    private var currentPlayingSong: Song? = null
    private var playbackState: PlaybackStateCompat? = null
    private var shouldUpdateSeekbar = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        ivPlayPauseDetail.setOnClickListener {
            currentPlayingSong?.let {
                mainViewModel.playOrToggle(it, true)
            }
        }

        ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }

        ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    setCurrentPlayerTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }

        })

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

        lifecycleScope.launchWhenStarted {
            mainViewModel.playbackState.collect {
                playbackState = it
                ivPlayPause.setImageResource(
                    if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
                )

                seekBar.progress = it?.position?.toInt() ?: 0
            }
        }

        lifecycleScope.launchWhenStarted {
            songViewModel.currentPlayerPosition.collect {
                if (shouldUpdateSeekbar) {
                    seekBar.progress = it.toInt()
                    setCurrentPlayerTime(it)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            songViewModel.currentSongDuration.collect {
                seekBar.max = it.toInt()
                val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
                tvSongDuration.text = dateFormat.format(it)
            }
        }
    }

    private fun setCurrentPlayerTime(ms: Long) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        tvCurTime.text = dateFormat.format(ms)
    }

    private fun updateTitleAndSongImage(song: Song) {
        val title = "${song.title} - ${song.artist}"
        tvSongName.text = title
        glide.load(song.albumArt).into(ivSongImage)
    }

}