package com.arjun.streamy.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.arjun.streamy.R
import com.arjun.streamy.data.entities.Song
import com.arjun.streamy.ui.MainViewModel
import com.arjun.streamy.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject
    internal lateinit var songAdapter: SongAdapter

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvAllSongs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songAdapter
        }

        lifecycleScope.launchWhenStarted {
            viewModel.mediaItems.collect {

                allSongsProgressBar.isVisible = it is Resource.Loading

                when (it) {
                    Resource.Loading -> Unit
                    is Resource.Success -> {
                        songAdapter.songs = (it.data as List<Song>)
                    }
                    is Resource.Error -> Unit
                }
            }
        }

        songAdapter.setOnItemClickListener {
            viewModel.playOrToggle(it)
        }

    }

}