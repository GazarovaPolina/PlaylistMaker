package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)

    @Delete(entity = PlaylistEntity::class)
    suspend fun deletePlaylist(playlist: PlaylistEntity): Int

    @Query("SELECT * FROM playlist_table")
    fun getListOfPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT playlistTracksIds FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun getTracksIds(playlistId: Long): String

    @Query ("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun getPlaylistDetailsById(playlistId: Long): PlaylistEntity

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>
}
