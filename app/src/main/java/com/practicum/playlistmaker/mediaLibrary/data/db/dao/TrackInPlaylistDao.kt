package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {
    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackToPlaylist(track: TrackInPlaylistEntity)

    @Query("SELECT * FROM track_in_playlist_table WHERE trackId IN (:tracksIdsList)")
    suspend fun getPlaylistTracksList(tracksIdsList: List<Int>): List<TrackInPlaylistEntity>

    @Delete(entity = TrackInPlaylistEntity::class)
    suspend fun deleteTrack(track : TrackInPlaylistEntity)

    @Query("SELECT * FROM track_in_playlist_table")
    suspend fun getTracksInAllPlaylists(): List<TrackInPlaylistEntity>
}