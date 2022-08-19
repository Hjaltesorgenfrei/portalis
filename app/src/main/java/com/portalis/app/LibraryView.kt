package com.portalis.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.portalis.app.database.BookRepository
import com.portalis.lib.ByteArrayImageSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LibraryViewModel @Inject constructor(
    val repository: BookRepository
) : ViewModel() {
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Library(
    navController: NavHostController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val listState: LazyListState = rememberLazyListState()
    val books by viewModel.repository.readAllData.observeAsState()
    books?.let {
        LazyVerticalGrid(
            state = listState,
            cells = GridCells.Fixed(2),
        ) {

            items(it) { book ->
                book?.let { b ->
                    BookCover(b.title, b.imageBytes?.let { ByteArrayImageSource(it) }, onClick = {
                    })
                }
            }
        }
    }
}

@ExperimentalFoundationApi
public fun <T : Any> LazyGridScope.items(
    items: List<T>,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    items(items.size) { index ->
        itemContent(items[index])
    }
}
