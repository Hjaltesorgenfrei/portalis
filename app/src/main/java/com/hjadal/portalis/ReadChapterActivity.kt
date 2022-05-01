package com.hjadal.portalis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.portalis.lib.HorizontalLine
import com.portalis.lib.ImageContent
import com.portalis.lib.TextContent

@Composable
fun ReadChapter() {
    val viewModel: ChapterModel = hiltViewModel()
    when (viewModel.uiState.loading) {
        true -> CenteredLoadingSpinner()
        false ->
            LazyColumn(
                modifier = Modifier.padding(horizontal =  8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(viewModel.uiState.chapterContent.content) { item ->
                    when (item) {
                        is TextContent -> Text(item.text)
                        is ImageContent -> AsyncImage(
                            model = item.url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                        is HorizontalLine -> {
                            println("Making HR")
                            Divider(thickness = 1.dp)
                        }
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
    }
}