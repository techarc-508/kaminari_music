package com.example.kaminarimusic.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kaminarimusic.data.local.dao.MusicDao
import com.example.kaminarimusic.data.local.entity.AlbumEntity
import com.example.kaminarimusic.data.local.entity.ArtistEntity
import com.example.kaminarimusic.data.local.entity.SongEntity
import com.example.kaminarimusic.data.local.entity.SyncOperationEntity

@Database(
    entities = [
        SongEntity::class,
        AlbumEntity::class,
        ArtistEntity::class,
        SyncOperationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KaminariDatabase : RoomDatabase() {

    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: KaminariDatabase? = null

        fun getDatabase(context: Context): KaminariDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KaminariDatabase::class.java,
                    "kaminari_music_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
