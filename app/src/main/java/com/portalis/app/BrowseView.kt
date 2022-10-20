package com.portalis.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.portalis.lib.Book
import com.portalis.lib.UrlImageSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BrowseViewModel @Inject constructor(
    val currentBook: CurrentBook,
    private val royalRoadParser: RoyalRoadParser,
    private val pager: BookPager
) : ViewModel() {

    val books = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 2),
        pagingSourceFactory = { pager }
    ).flow.cachedIn(viewModelScope)
}

@Composable
fun Browse(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val listState = rememberLazyGridState()
    val books: LazyPagingItems<Book> = viewModel.books.collectAsLazyPagingItems()
    when (books.itemCount) {
        0 -> CenteredLoadingSpinner()
        else -> LazyVerticalGrid(
            state = listState,
            columns = GridCells.Fixed(2),
            contentPadding = paddingValues
        ) {
            items(books.itemCount) { index ->
                val book = books[index]
                book?.let { b ->
                    BookCover(b.title, UrlImageSource(b.imageUri), onClick = {
                        viewModel.currentBook.book = book
                        navController.navigate("book_screen")
                    })
                }
            }
        }
    }
}
