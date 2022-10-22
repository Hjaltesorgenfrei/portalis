package com.portalis.app

import androidx.compose.foundation.layout.PaddingValues
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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BrowseViewModel @Inject constructor(
    val currentBook: CurrentBook,
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
    val books: LazyPagingItems<Book> = viewModel.books.collectAsLazyPagingItems()
    when (books.itemCount) {
        0 -> CenteredLoadingSpinner()
        else -> PaddedLazyGrid(paddingValues, books) { b ->
            BookCover(b.title, UrlImageSource(b.imageUri), onClick = {
                viewModel.currentBook.book = b
                navController.navigate("book_screen")
            })
        }
    }
}
