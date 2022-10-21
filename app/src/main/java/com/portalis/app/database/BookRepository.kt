package com.portalis.app.database

import androidx.lifecycle.LiveData

class BookRepository(private val dao: BookDatabaseDao) {
    val readAllData: LiveData<List<BookItem>> = dao.getAll()

    suspend fun getById(id: String) : BookItem? {
        return dao.getById(id)
    }

    suspend fun addBook(bookItem: BookItem) {
        dao.insert(bookItem)
    }

    suspend fun updateBook(bookItem: BookItem) {
        dao.update(bookItem)
    }

    suspend fun deleteBook(bookItem: BookItem) {
        dao.delete(bookItem)
    }
}