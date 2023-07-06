package com.portalis.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.portalis.lib.HorizontalLine
import com.portalis.lib.ImageContent
import com.portalis.lib.TextContent

@Composable
fun ReadChapter() {
    val viewModel: ChapterModel = hiltViewModel()
    val listState = rememberLazyListState()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset, available: Offset, source: NestedScrollSource
            ): Offset {
                viewModel.scrollProgress(listState.firstVisibleItemIndex)
                return super.onPostScroll(consumed, available, source)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                viewModel.scrollProgress(listState.firstVisibleItemIndex)
                return super.onPostFling(consumed, available)
            }
        }
    }

    when (viewModel.uiState.loading) {
        true -> CenteredLoadingSpinner()
        false -> LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp),
            state = listState,
            modifier = Modifier.nestedScroll(nestedScrollConnection)
        ) {
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
            if (viewModel.hasNextChapter()) {
                item {
                    Row(horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                viewModel.nextChapter(listState)
                            }) {
                        Text(
                            "Next Chapter", fontWeight = FontWeight.Bold, fontSize = 16.sp
                        )
                        Icon(
                            Icons.Filled.ArrowRightAlt,
                            "Next Chapter",
                            Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}