package com.portalis.app

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.portalis.app.database.SourceDatabase
import com.portalis.app.database.SourceDatabaseDao
import com.portalis.app.database.SourceItem
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TodoDatabaseTest {

    private lateinit var sourceDao: SourceDatabaseDao
    private lateinit var db: SourceDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, SourceDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        sourceDao = db.sourceDao()
    }

    @After
    @Throws(IOException::class)
    fun deleteDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTodo() = runBlocking {
        val todoItem = SourceItem(itemId = 1, itemName = "Dummy Item")
        sourceDao.insert(todoItem)
        val oneItem = sourceDao.getById(1)
        assertEquals(1L, oneItem?.itemId)
    }
}