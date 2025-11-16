package com.example.plugd.data.localRoom.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.plugd.data.localRoom.dao.*
import com.example.plugd.data.localRoom.entity.*

@Database(
    entities = [
        UserEntity::class,
        EventEntity::class,
        UserProfileEntity::class,
        ChannelEntity::class,
        MessageEntity::class,
        ActivityEntity::class
    ],
    version = 20,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun chatDao(): ChatDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plugd_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}













/*package com.example.plugd.data.localRoom.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.plugd.data.localRoom.dao.ActivityDao
import com.example.plugd.data.localRoom.dao.ChatDao
import com.google.firebase.firestore.FirebaseFirestore
import com.example.plugd.data.localRoom.dao.UserDao
import com.example.plugd.data.localRoom.dao.EventDao
import com.example.plugd.data.localRoom.dao.UserProfileDao
import com.example.plugd.data.localRoom.entity.ActivityEntity
import com.example.plugd.data.localRoom.entity.MessageEntity
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.data.localRoom.entity.ChannelEntity
import com.example.plugd.data.localRoom.entity.UserProfileEntity

@Database(entities = [UserEntity::class, EventEntity::class, UserProfileEntity::class, ChannelEntity::class, MessageEntity::class, ActivityEntity::class], version = 9, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun chatDao(): ChatDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plugd_db"
                )
                    .fallbackToDestructiveMigration() // reset DB if schema changed
                    .build()
                INSTANCE = instance
                instance

            }
        }
    }
}*/