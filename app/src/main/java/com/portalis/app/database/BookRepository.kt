package com.portalis.app.database

import androidx.lifecycle.LiveData

class BookRepository(private val dao: BookDatabaseDao) {
    val readAllData: LiveData<List<BookItem>> = dao.getAll()

    suspend fun addBook(bookItem: BookItem) {
        dao.insert(bookItem)
    }

    suspend fun updateSource(bookItem: BookItem) {
        dao.update(bookItem)
    }

    suspend fun deleteSource(bookItem: BookItem) {
        dao.delete(bookItem)
    }

    suspend fun deleteAllTodos() {
        dao.deleteAllSources()
    }
}