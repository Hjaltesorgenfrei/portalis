package com.portalis.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SourceItem::class], version = 1)
abstract class SourceDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDatabaseDao

    companion object {
        private var INSTANCE: SourceDatabase? = null
        fun getInstance(context: Context): SourceDatabase {
            synchronized(SourceDatabase::class) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SourceDatabase::class.java,
                        "source_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}