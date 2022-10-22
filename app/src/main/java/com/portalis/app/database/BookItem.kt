package com.portalis.app.database

import android.R.attr.data
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.portalis.app.ImageSource
import com.portalis.lib.Book
import java.io.ByteArrayOutputStream


@Entity(tableName = "books")
class BookItem(
    @PrimaryKey
    var itemId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "image_bytes")
    val imageBytes: ByteArray? = null
)

fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    val width = bm.width
    val height = bm.height
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    // CREATE A MATRIX FOR THE MANIPULATION
    val matrix = Matrix()
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight)

    // "RECREATE" THE NEW BITMAP
    return Bitmap.createBitmap(
        bm, 0, 0, width, height, matrix, false
    )
}


fun toBookItem(book: Book, bitmap: Bitmap?) : BookItem {
    val image = bitmap?.let {
        val resized = getResizedBitmap(it, 400, 600)
        val bos = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.PNG, 100, bos)
        bos.toByteArray()
    }
    return BookItem(book.uri, book.title, image)
}


public fun ByteArray?.toBitMap(): Bitmap? {
    return this?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
}