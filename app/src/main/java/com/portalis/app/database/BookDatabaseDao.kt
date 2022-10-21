package com.portalis.app.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BookDatabaseDao {
    @Query("SELECT * from books")
    fun getAll(): LiveData<List<BookItem>>

    @Query("SELECT * from books where itemId = :id")
    suspend fun getById(id: String): BookItem?

    @Insert
    suspend fun insert(item: BookItem)

    @Update
    suspend fun update(item: BookItem)

    @Delete
    suspend fun delete(item: BookItem)

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()
}