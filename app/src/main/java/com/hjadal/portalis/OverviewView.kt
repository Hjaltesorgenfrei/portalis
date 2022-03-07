package com.hjadal.portalis

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException
import javax.inject.Inject


data class OverviewUiState(
    val books: List<Book> = emptyList(),
    val loading: Boolean = true
)

@HiltViewModel
class OverviewModel @Inject constructor(
    val currentBook: CurrentBook,
    val currentChapter: CurrentChapter
) : ViewModel() {

    fun booksReady(books: List<Book>) {
        uiState = OverviewUiState(books, false)
    }

    var uiState by mutableStateOf(OverviewUiState())
        private set

    init {
        loadBooks(this)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Overview(
    navController: NavHostController,
    viewModel: OverviewModel = hiltViewModel()
) {
    when (viewModel.uiState.loading) {
        true -> CenteredLoadingSpinner()
        false -> LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(all = 16.dp)
        ) {
            items(viewModel.uiState.books) { book ->
                Cover(book)
            }
        }
    }
}

@Composable
private fun Cover(book: Book) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 0.75f, matchHeightConstraintsFirst = false)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.BottomStart,
    ) {
        AsyncImage(
            model = book.imageUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Text(
            text = book.title,
            modifier = Modifier.padding(8.dp),
            color = Color.White,
            fontSize = 16.sp,
        )
    }
}

private fun loadBooks(viewModel: OverviewModel) {
    NetUtil.run("https://www.royalroad.com/fictions/trending", object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            val result = response.body?.string()
            val doc = Jsoup.parse(result as String)
            val elements = doc.getElementsByClass("fiction-list-item row")
            val books = elements.toList()
                .map { e ->
                    val title = e.getElementsByClass("fiction-title")[0].text()
                    val imageUri = e.getElementsByTag("img")[0].attr("src")
                    Book(title, "", imageUri)
                }
            viewModel.booksReady(books)
        }
    })
}