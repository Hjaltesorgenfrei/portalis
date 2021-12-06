package com.hjadal.portalis

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PortalisTheme.Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BookScreen()
                }
            }
        }
        loadChapters()
    }

    private fun switchToChapterView(chapter: Chapter) {
        val intent = Intent(this, ReadChapterActivity::class.java).apply {
            putExtra("chapter", chapter)
        }
        startActivity(intent)
    }

    private fun loadChapters() {
        NetUtil.run("https://www.royalroad.com/fiction/22518/chrysalis", object : Callback {
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
                val viewModel by viewModels<BookModel>()
                viewModel.chaptersReady(chapters)
            }
        })
    }
}

data class BookUiState(
    val chapters: List<Chapter> = emptyList(),
    val loading: Boolean = true
)

class BookModel : ViewModel() {
    fun chaptersReady(chapters: List<Chapter>) {
        uiState = BookUiState(chapters, false)
    }

    var uiState by mutableStateOf(BookUiState())
        private set
}

@Composable
private fun BookScreen(
    viewModel: BookModel = viewModel()
) {
    when (viewModel.uiState.loading) {
        true -> Text("Loading")
        false -> ChaptersScreen(viewModel.uiState.chapters)
    }
}

@Composable
private fun HeaderView() {
    Icon(Icons.Filled.Download, "Info", Modifier.size(100.dp))
}

@Composable
private fun ChaptersScreen(chapters: List<Chapter>) {
    LazyColumn {
        item {
            HeaderView()
        }
        items(chapters) { chapter ->
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
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.primary
                    )
                    Text("12/07/2021", color = Color.Gray)
                }
                Icon(
                    Icons.Filled.Download,
                    "Download Chapter",
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { println("Download " + chapter.title) }
                        .padding(8.dp))
            }
        }
    }
}

@Preview
@Composable
fun ComposablePreview() {
    ChaptersScreen(
        listOf(
            Chapter("Great", "--", "--"),
            Chapter("Chapter 2 - Harry goes to School", "--", "--")
        )
    )
}


