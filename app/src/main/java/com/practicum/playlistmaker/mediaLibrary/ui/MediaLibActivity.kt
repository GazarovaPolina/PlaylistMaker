package com.practicum.playlistmaker.mediaLibrary.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaLibBinding

class MediaLibActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMediaLibBinding

    private lateinit var tabMediator: TabLayoutMediator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mediaLibViewPager.adapter = MediaLibViewPagerAdapter(this, supportFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(binding.mediaLibTabLayout, binding.mediaLibViewPager) {tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()

        binding.toolbarMediaLib.setNavigationOnClickListener{
            finish()
        }
    }

    override fun onDestroy() {
        tabMediator.detach()
        super.onDestroy()
    }
}