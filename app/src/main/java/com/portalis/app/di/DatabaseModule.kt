package com.portalis.app.di

import android.content.Context
import androidx.room.Room
import com.portalis.app.database.BookDatabase
import com.portalis.app.database.BookDatabaseDao
import com.portalis.app.database.BookRepository
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
    fun provideBookDao(bookDatabase: BookDatabase): BookDatabaseDao {
        return bookDatabase.bookDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): BookDatabase {
        return Room.databaseBuilder(
            appContext,
            BookDatabase::class.java,
            "source_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providesBookRepository(sourceDao: BookDatabaseDao): BookRepository =
        BookRepository(sourceDao)
}