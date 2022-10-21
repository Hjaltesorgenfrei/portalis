package com.portalis.app

import androidx.compose.foundation.layout.PaddingValues
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


@Composable
fun Library(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val books by viewModel.repository.readAllData.observeAsState()
    books?.let {
        PaddedLazyGrid(paddingValues, it) { b ->
            BookCover(b.title, b.imageBytes?.let { bytes -> ByteArrayImageSource(bytes) }, onClick = {
                println(b.title)
            })
        }
    }
}