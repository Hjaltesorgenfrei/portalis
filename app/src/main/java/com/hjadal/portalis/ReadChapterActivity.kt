package com.hjadal.portalis

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Composable
fun ReadChapter() {
    val viewModel: ChapterModel = hiltViewModel()
    when (viewModel.uiState.loading) {
        true -> CenteredLoadingSpinner()
        false -> Text(
            viewModel.uiState.chapterContent,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}