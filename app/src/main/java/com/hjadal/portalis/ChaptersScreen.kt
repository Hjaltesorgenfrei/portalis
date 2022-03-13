package com.hjadal.portalis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.jsoup.Jsoup
import com.portalis.lib.Book
import com.portalis.lib.Chapter
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


internal data class BookUiState(
    val chapters: List<Chapter> = emptyList(),
    val loading: Boolean = true
)

@Singleton
class CurrentBook @Inject constructor() {
    var book: Book? = null
}

@HiltViewModel
class BookModel @Inject constructor(
    private val currentBook: CurrentBook,
    val currentChapter: CurrentChapter
) : ViewModel() {

    fun chaptersReady(chapters: List<Chapter>) {
        uiState = BookUiState(chapters, false)
    }

    internal var uiState by mutableStateOf(BookUiState())
        private set

    val url = currentBook.book?.uri

    init {
        loadChapters(this)
    }
}

@Composable
fun BookScreen(
    navController: NavHostController,
    viewModel: BookModel = hiltViewModel()
) {
    when (viewModel.uiState.loading) {
        true -> CenteredLoadingSpinner()
        false -> ChaptersScreen(navController)
    }
}

private fun loadChapters(viewModel: BookModel) {
    viewModel.url?.let {
        NetUtil.run(it, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val doc = Jsoup.parse(result as String)
                val elements = doc.getElementsByClass("chapter-row")
                val chapters = elements.toList()
                    .map { e ->
                        val title = e.getElementsByTag("a")[0].text()
                        val uri = "https://www.royalroad.com" + e.attr("data-url")
                        val number =
                            e.getElementsByAttribute("data-content")[0].attr("data-content")
                        Chapter(title, uri, number)
                    }
                viewModel.chaptersReady(chapters)
                println(chapters.size.toString() + " chapters read")
            }
        })
    }
}

@Composable
private fun HeaderView() {
    Icon(Icons.Filled.Download, "Info", Modifier.size(100.dp))
}

@Composable
private fun ChaptersScreen(
    navController: NavController,
    viewModel: BookModel = hiltViewModel()
) {
    LazyColumn {
        item {
            HeaderView()
        }
        items(viewModel.uiState.chapters) { chapter ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        chapter.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text("12/07/2021", color = Color.Gray)
                }
                Icon(
                    Icons.Filled.Download,
                    "Download Chapter",
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            viewModel.currentChapter.chapter = chapter
                            navController.navigate("read_chapter")
                        }
                        .padding(8.dp))
            }
        }
    }
}

