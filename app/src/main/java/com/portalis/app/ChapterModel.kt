package com.portalis.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import com.portalis.lib.*
import dagger.hilt.android.lifecycle.HiltViewModel
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
    var chapter: Chapter? = null
}

data class ChapterUiState(
    val chapterContent: ChapterContent = ChapterContent(listOf()),
    val loading: Boolean = true
)

@HiltViewModel
class ChapterModel @Inject constructor(
    currentChapter: CurrentChapter,
    private val royalRoadParser: RoyalRoadParser
) : ViewModel() {
    fun chapterReady(chapterContent: ChapterContent) {
        uiState = ChapterUiState(chapterContent, false)
    }

    var uiState by mutableStateOf(ChapterUiState())
        private set

    init {
        currentChapter.chapter?.let { loadChapter(it.uri, this, royalRoadParser.parser) }
    }
}

private fun loadChapter(encodedUri: String, viewModel: ChapterModel, parser: Parser) {
    val uri = parser.chapter(URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString()))
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