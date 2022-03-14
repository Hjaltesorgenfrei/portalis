package com.portalis.lib

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

class Parser(input: String) {
    fun parse(htmlContent: String): List<Book> {
        val doc = Jsoup.parse(htmlContent)
        val elements = doc.select(bookSelector.book)
        println("Found ${elements.size} books")
        val books = elements.toList()
            .map { e ->
                val title = e.select(bookSelector.title)[0]
                val href = title.select(bookSelector.uri)[0].attr("href")
                val uri = "https://www.royalroad.com$href"
                val imageUri = e.select(bookSelector.imageUri)[0].attr("src")
                Book(title.text(), uri, imageUri)
            }
        return books
    }

    private val bookSelector: BookSelector = Json.decodeFromString(input)
}

@Serializable
class BookSelector(val book: String, val title: String, val uri: String, val imageUri: String)