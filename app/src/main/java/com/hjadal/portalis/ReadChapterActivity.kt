package com.hjadal.portalis

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun ReadChapter(encodedUri: String) {
    val viewModel: ChapterModel =
        viewModel(factory = ChapterViewModelFactory(encodedUri))
    when (viewModel.uiState.loading) {
        true -> Text("Loading")
        false -> Text(
            viewModel.uiState.chapterContent,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}

data class ChapterUiState(
    val chapterContent: String = "",
    val loading: Boolean = true
)

class ChapterModel(uri: String) : ViewModel() {
    fun chapterReady(chapterContent: String) {
        uiState = ChapterUiState(chapterContent, false)
    }

    var uiState by mutableStateOf(ChapterUiState())
        private set

    init {
        loadChapter(uri, this)
    }
}

@Suppress("UNCHECKED_CAST")
class ChapterViewModelFactory(private val uri: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ChapterModel(uri) as T
}

private fun loadChapter(encodedUri: String, viewModel: ChapterModel) {
    val uri = URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString())
    NetUtil.run(uri, object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            val result = response.body?.string()
            val doc = Jsoup.parse(result as String)
            val chapterHtml = doc.getElementsByClass("chapter-content")[0].html()
            val chapterText =
                HtmlCompat.fromHtml(chapterHtml, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            viewModel.chapterReady(chapterText)
            println("Downloaded chapter")
        }
    })
}