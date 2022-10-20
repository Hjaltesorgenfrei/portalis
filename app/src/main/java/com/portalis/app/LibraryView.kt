package com.portalis.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.portalis.app.database.BookRepository
import com.portalis.lib.ByteArrayImageSource
import com.portalis.lib.UrlImageSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LibraryViewModel @Inject constructor(
    val repository: BookRepository
) : ViewModel() {
}


@Composable
fun Library(
    navController: NavHostController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val listState = rememberLazyGridState()
    val books by viewModel.repository.readAllData.observeAsState()
    books?.let {
        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Fixed(2),
        ) {
            items(it.size) { index ->
                val book = it[index]
                book?.let { b ->
                    BookCover(b.title, b.imageBytes?.let { ByteArrayImageSource(it) }, onClick = {
                    })
                }
            }
        }
    }
}