package com.practicum.playlistmaker.mediaLibrary.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.ui.fragments.MediaLibFragmentFavorites
import com.practicum.playlistmaker.mediaLibrary.ui.fragments.MediaLibFragmentPlaylists

class MediaLibViewPagerAdapter(context: Context, fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val favoritesMessage: String
    private val playlistsMessage: String
    init {
       favoritesMessage = context.getString(R.string.media_lib_favorites_placeholder_text)
        playlistsMessage = context.getString(R.string.media_lib_playlists_placeholder_text)
    }
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)   {
            MediaLibFragmentFavorites.newInstance(favoritesMessage)
        } else {
            MediaLibFragmentPlaylists.newInstance(playlistsMessage)
        }
    }
}