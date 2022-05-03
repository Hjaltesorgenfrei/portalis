package com.portalis.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.portalis.lib.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


data class OverviewUiState(
    val books: List<Book> = emptyList(),
    val loading: Boolean = true
)

@HiltViewModel
class OverviewModel @Inject constructor(
    val currentBook: CurrentBook,
    private val royalRoadParser: RoyalRoadParser,
    private val pager: BookPager
) : ViewModel() {

    val books = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 2),
        pagingSourceFactory = { BookPager(royalRoadParser) }
    ).flow.cachedIn(viewModelScope)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Overview(
    navController: NavHostController,
    viewModel: OverviewModel = hiltViewModel()
) {
    val listState: LazyListState = rememberLazyListState()
    val books: LazyPagingItems<Book> = viewModel.books.collectAsLazyPagingItems()
    when (books.itemCount) {
        0 -> CenteredLoadingSpinner()
        else -> LazyVerticalGrid(
            state = listState,
            cells = GridCells.Fixed(2),
        ) {
            items(books) { book ->
                book?.let { b ->
                    Cover(b, onClick = {
                        viewModel.currentBook.book = book
                        navController.navigate("book_screen")
                    })
                }
            }
        }
    }
}

@ExperimentalFoundationApi
public fun <T : Any> LazyGridScope.items(
    lazyPagingItems: LazyPagingItems<T>,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    items(lazyPagingItems.itemCount) { index ->
        itemContent(lazyPagingItems[index])
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