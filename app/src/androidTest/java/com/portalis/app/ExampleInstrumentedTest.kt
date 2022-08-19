package com.portalis.app

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.portalis.app.database.BookDatabase
import com.portalis.app.database.BookDatabaseDao
import com.portalis.app.database.BookItem
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class TodoDatabaseTest {

    private lateinit var sourceDao: BookDatabaseDao
    private lateinit var db: BookDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, BookDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        sourceDao = db.bookDao()
    }

    @After
    @Throws(IOException::class)
    fun deleteDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTodo() = runBlocking {
        val todoItem = BookItem(UUID.randomUUID().toString(), title = "Dummy Item")
        sourceDao.insert(todoItem)
        val oneItem = sourceDao.getById(1)
        assertEquals(1L, oneItem?.itemId)
    }
}