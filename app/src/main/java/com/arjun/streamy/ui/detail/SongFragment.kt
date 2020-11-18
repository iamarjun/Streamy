package com.arjun.streamy.ui.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.arjun.streamy.R

class SongFragment : Fragment(R.layout.song_fragment) {

    private val viewModel by viewModels<SongViewModel>()

}