package com.portalis.lib

import com.portalis.lib.schema.Comment
import com.portalis.lib.schema.Format
import com.portalis.lib.schema.Formats
import com.portalis.lib.schema.Pattern
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class Parser(input: String) {
    fun parseOverview(htmlContent: String): List<Book> {
        val doc = Jsoup.parse(htmlContent)
        val overviewSelector = sourceParser.overviewSelector
        val elements = doc.select(overviewSelector.book)
        val books = elements.toList()
            .map { e ->
                val title = e.select(overviewSelector.bookTitle)[0].text()
                val href = overviewSelector.bookUri.selectAndGet(e)
                val uri = "${sourceParser.baseurl}${href}"
                val imageUri = e.select(overviewSelector.bookImageUri)[0].attr("src")
                Book(title, uri, imageUri)
            }
        return books
    }

    fun parseBook(htmlContent: String, url: String): Book {
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
        return Book(title, url, imageUri, chapters, author, description)
    }

    private val jsonFormat: Json = Json { ignoreUnknownKeys = true }

    private val sourceParser: Source = jsonFormat.decodeFromString(input)
    fun chapter(path: String): String {
        if (path.startsWith("/")) {
            return sourceParser.baseurl + path
        }
        return path
    }

    fun getTopRatedPage(pageNumber: Int): String {
        val queryString = sourceParser.topRated?.queryString
        return sourceParser.baseurl +
                (queryString?.replace("{{pageNumber}}", pageNumber.toString()))
    }
}

@Serializable
class Source(
    @property:Format(Formats.URI)
    val baseurl: String,
    val topRated: PaginationDefinition?,
    val overviewSelector: OverviewSelector,
    val bookSelector: BookSelector
)

@Serializable
class PaginationDefinition(
    @property:Pattern("{{pageNumber}}")
    @property:Comment("{{pageNumber}} by the incrementing page number")
    val queryString: String,
    val startPage: Int
)

@Serializable
class OverviewSelector(
    val book: String,
    val bookTitle: String,
    val bookUri: ElementValueSelector,
    val bookImageUri: String
)

@Serializable
sealed class ElementValueSelector() {
    abstract val selector: String
    abstract fun getValue(element: Element) : String
    fun selectAndGet(parentElement: Element) : String {
        return getValue(parentElement.select(selector)[0])
    }
}

@Serializable
class TextValueSelector(
    override val selector: String
) : ElementValueSelector() {
    override fun getValue(element: Element): String {
        return element.text()
    }
}

@Serializable
class AttributeValueSelector(
    override val selector: String,
    val attribute: String
) : ElementValueSelector() {
    override fun getValue(element: Element): String {
        return element.attr(attribute)
    }
}

@Serializable
class BookSelector(
    val title: String,
    val author: String,
    val description: String,
    val imageUri: String,
    val chapter: String,
    val chapterTitle: String,
    val chapterUri: String,
    val chapterDate: String?
)