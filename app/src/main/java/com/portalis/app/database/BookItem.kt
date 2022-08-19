package com.portalis.app.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
class BookItem(
    @PrimaryKey
    var itemId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "image_bytes")
    val imageBytes: ByteArray? = null
)