package com.practicum.playlistmaker.mediaLibrary.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.ui.fragments.MediaLibFavoritesFragment
import com.practicum.playlistmaker.mediaLibrary.ui.fragments.MediaLibPlaylistsFragment

class MediaLibViewPagerAdapter(context: Context, fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val favoritesMessage: String = context.getString(R.string.media_lib_favorites_placeholder_text)
    private val playlistsMessage: String = context.getString(R.string.media_lib_playlists_placeholder_text)

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = if (position == 0) {
        MediaLibFavoritesFragment.newInstance(favoritesMessage)
    } else {
        MediaLibPlaylistsFragment.newInstance(playlistsMessage)
    }
}