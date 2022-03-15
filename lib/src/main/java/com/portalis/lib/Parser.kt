package com.portalis.lib

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

class Parser(input: String) {
    fun parseOverview(htmlContent: String): List<Book> {
        val doc = Jsoup.parse(htmlContent)
        val overviewSelector = sourceParser.overviewSelector
        val elements = doc.select(overviewSelector.book)
        val books = elements.toList()
            .map { e ->
                val title = e.select(overviewSelector.bookTitle)[0].text()
                val href = e.select(overviewSelector.bookUri)[0].attr("href")
                val uri = "${sourceParser.baseurl}${href}"
                val imageUri = e.select(overviewSelector.bookImageUri)[0].attr("src")
                Book(title, uri, imageUri)
            }
        return books
    }

    fun parseBook(htmlContent: String): Book {
        val doc = Jsoup.parse(htmlContent)
        val bookSelector = sourceParser.bookSelector
        val title = doc.select(bookSelector.title)[0].text()
        val author = doc.select(bookSelector.author)[0].text()
        val description = doc.select(bookSelector.description)[0].text()
        val imageUri = doc.select(bookSelector.imageUri)[0].attr("src")
        val chapters = doc.select(bookSelector.chapter).toList().mapIndexed { index, e ->
            val chapterTitle = e.select(bookSelector.chapterTitle)[0].text()
            val chapterUri = e.select(bookSelector.chapterUri)[0].attr("href")
            val chapterDate = e.select(bookSelector.chapterDate)[0].text()
            Chapter(chapterTitle, chapterUri, index.toString(), chapterDate)
        }
        return Book(title, "", imageUri, chapters)
    }

    private val sourceParser: Source = Json.decodeFromString(input)
    val topRated: String = sourceParser.baseurl + sourceParser.topRated
}

@Serializable
class Source(
    val baseurl: String,
    val topRated: String,
    val overviewSelector: OverviewSelector,
    val bookSelector: BookSelector
)

@Serializable
class OverviewSelector(
    val book: String,
    val bookTitle: String,
    val bookUri: String,
    val bookImageUri: String
)

@Serializable
class BookSelector(
    val title: String,
    val author: String,
    val description: String,
    val imageUri: String,
    val chapter: String,
    val chapterTitle: String,
    val chapterUri: String,
    val chapterDate: String
)