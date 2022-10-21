package com.portalis.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> PaddedLazyGrid(
    paddingValues: PaddingValues,
    items: LazyPagingItems<T>,
    content: @Composable (T) -> Unit
) {
    val listState = rememberLazyGridState()
    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Fixed(2)
    ) {
        items(2) {
            Spacer(modifier = Modifier.padding(top = paddingValues.calculateTopPadding()))
        }
        items(items.itemCount) { index ->
            items[index]?.let { content(it) }
        }
    }
}

@Composable
fun <T : Any> PaddedLazyGrid(
    paddingValues: PaddingValues,
    items: List<T>,
    content: @Composable (T) -> Unit
) {
    val listState = rememberLazyGridState()
    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Fixed(2)
    ) {
        items(2) {
            Spacer(modifier = Modifier.padding(top = paddingValues.calculateTopPadding()))
        }
        items(items.size) { index ->
            content(items[index])
        }
    }
}