package com.hjadal.portalis.database

import androidx.lifecycle.LiveData

class SourceRepository(private val dao: SourceDatabaseDao) {
    val readAllData: LiveData<List<SourceItem>> = dao.getAll()

    suspend fun addSource(sourceItem: SourceItem) {
        dao.insert(sourceItem)
    }
    suspend fun updateSource(sourceItem: SourceItem) {
        dao.update(sourceItem)
    }
    suspend fun deleteSource(sourceItem: SourceItem) {
        dao.delete(sourceItem)
    }
    suspend fun deleteAllTodos() {
        dao.deleteAllSources()
    }
}