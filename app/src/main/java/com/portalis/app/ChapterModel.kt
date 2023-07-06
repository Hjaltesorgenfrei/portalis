package com.portalis.app

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portalis.lib.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.parser.Tag
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentChapter @Inject constructor() {
    var chapter: Int? = null
}

data class ChapterUiState(
    val chapterContent: ChapterContent = ChapterContent(listOf()),
    val loading: Boolean = true
)

@HiltViewModel
class ChapterModel @Inject constructor(
    private val currentChapter: CurrentChapter,
    private val currentBook: CurrentBook,
    private val royalRoadParser: RoyalRoadParser
) : ViewModel() {
    fun chapterReady(chapterContent: ChapterContent) {
        uiState = ChapterUiState(chapterContent, false)
    }

    fun hasNextChapter(): Boolean {
        return currentChapter.chapter!! < currentBook.book!!.chapters.size - 1
    }

    fun nextChapter(listState: LazyListState) {
        if (!hasNextChapter()) {
            return
        }
        uiState = ChapterUiState(ChapterContent(emptyList()), true)
        currentChapter.chapter = currentChapter.chapter!! + 1
        viewModelScope.launch {
            listState.scrollToItem(0, 0)
        }
        loadChapter(currentBook.book!!.chapters[currentChapter.chapter!!].uri, this, royalRoadParser.parser)
    }

    var uiState by mutableStateOf(ChapterUiState())
        private set

    init {
        loadChapter(currentBook.book!!.chapters[currentChapter.chapter!!].uri, this, royalRoadParser.parser)
    }
}

private fun loadChapter(encodedUri: String, viewModel: ChapterModel, parser: Parser) {
    val uri = parser.prependBaseIfRelative(URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString()))
    NetUtil.run(uri, object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            val result = response.body?.string()
            val doc = Jsoup.parse(result as String)
            val chapterContent = doc.getElementsByClass("chapter-content")[0]
            val chapterText = chapterContent.select("p, hr").map {
                when {
                    it.select("img").any() -> {
                        ImageContent(it.select("img").attr("src"))
                    }
                    it.tag() == Tag.valueOf("hr") -> {
                        HorizontalLine
                    }
                    else -> {
                        val text = HtmlCompat.fromHtml(it.html(), HtmlCompat.FROM_HTML_MODE_LEGACY)
                            .toString()
                        TextContent(text)
                    }
                }
            }
            viewModel.chapterReady(ChapterContent(chapterText))
            println("Downloaded chapter")
        }
    })
}