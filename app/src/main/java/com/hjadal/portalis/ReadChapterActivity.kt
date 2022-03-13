package com.hjadal.portalis

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReadChapter() {
    val viewModel: ChapterModel = hiltViewModel()
    when (viewModel.uiState.loading) {
        true -> CenteredLoadingSpinner()
        false -> Text(
            viewModel.uiState.chapterContent,
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 8.dp)
        )
    }
}