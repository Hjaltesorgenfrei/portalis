package com.hjadal.portalis.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SourceDatabaseDao {
    @Query("SELECT * from sources")
    fun getAll(): LiveData<List<SourceItem>>

    @Query("SELECT * from sources where itemId = :id")
    fun getById(id: Int) : SourceItem?

    @Insert
    suspend fun insert(item:SourceItem)

    @Update
    suspend fun update(item:SourceItem)

    @Delete
    suspend fun delete(item:SourceItem)

    @Query("DELETE FROM sources")
    suspend fun deleteAllSources()
}