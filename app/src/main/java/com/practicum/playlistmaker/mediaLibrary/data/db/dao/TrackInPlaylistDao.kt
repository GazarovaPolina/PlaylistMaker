package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {
    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackToPlaylist(track: TrackInPlaylistEntity)
}