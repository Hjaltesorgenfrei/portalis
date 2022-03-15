package com.portalis.lib

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

class Parser(input: String) {
    fun parse(htmlContent: String): List<Book> {
        val doc = Jsoup.parse(htmlContent)
        val bookSelector = sourceParser.bookSelector
        val elements = doc.select(bookSelector.selector)
        println("Found ${elements.size} books")
        val books = elements.toList()
            .map { e ->
                val title = e.select(bookSelector.title)[0].text()
                val href = e.select(bookSelector.uri)[0].attr("href")
                val uri = "${sourceParser.baseurl}${href}"
                val imageUri = e.select(bookSelector.imageUri)[0].attr("src")
                Book(title, uri, imageUri)
            }
        return books
    }

    private val sourceParser: Source = Json.decodeFromString(input)
    val topRated: String = sourceParser.baseurl + sourceParser.topRated
}

@Serializable
class Source(val baseurl: String, val topRated: String, val bookSelector: BookSelector)

@Serializable
class BookSelector(val selector: String, val title: String, val uri: String, val imageUri: String)