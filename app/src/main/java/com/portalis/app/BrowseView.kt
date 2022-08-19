package com.portalis.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Browse(
    navController: NavHostController,
    viewModel: BrowseViewModel = hiltViewModel()
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
                    BookCover(b.title, UrlImageSource(b.imageUri), onClick = {
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

