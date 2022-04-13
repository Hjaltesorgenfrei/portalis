package com.hjadal.portalis

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.portalis.lib.Book
import com.portalis.lib.NetUtil
import com.portalis.lib.Parser
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
    private val royalRoadParser: RoyalRoadParser
) : ViewModel() {

    fun booksReady(books: List<Book>) {
        uiState = OverviewUiState(books, false)
    }

    var uiState by mutableStateOf(OverviewUiState())
        private set

    init {
        loadBooks(this, royalRoadParser.parser)
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
        ) {
            items(viewModel.uiState.books) { book ->
                Cover(book, onClick = {
                    viewModel.currentBook.book = book
                    navController.navigate("book_screen")
                })
            }
        }
    }
}

@Composable
private fun Cover(book: Book, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
            .aspectRatio(ratio = 0.75f, matchHeightConstraintsFirst = false)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomStart,
    ) {
        AsyncImage(
            model = book.imageUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(128.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black
                        )
                    )
                )
        )
        Text(
            text = book.title,
            modifier = Modifier.padding(8.dp),
            color = Color.White,
            fontSize = 16.sp,
        )
    }
}

private fun loadBooks(viewModel: OverviewModel, parser: Parser) {

    NetUtil.run(parser.topRated, object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.let { r ->
                val books = parser.parseOverview(r.string())
                viewModel.booksReady(books)
            }
        }
    })
}