package com.portalis.app.di

import android.content.Context
import androidx.room.Room
import com.portalis.app.database.SourceDatabase
import com.portalis.app.database.SourceDatabaseDao
import com.portalis.app.database.SourceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideExampleDao(sourceDatabase: SourceDatabase): SourceDatabaseDao {
        return sourceDatabase.sourceDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): SourceDatabase {
        return Room.databaseBuilder(
            appContext,
            SourceDatabase::class.java,
            "source_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providesExampleRepository(sourceDao: SourceDatabaseDao): SourceRepository =
        SourceRepository(sourceDao)
}