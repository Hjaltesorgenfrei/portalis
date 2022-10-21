package com.portalis.app

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.portalis.lib.Book
import com.portalis.lib.NetUtil
import com.portalis.lib.Parser
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


val MINIMIZED_MAX_LINES: Int = 4

internal data class BookUiState(
    val book: Book? = null
)

@Singleton
class CurrentBook @Inject constructor() {
    var book: Book? = null
}

@HiltViewModel
class BookModel @Inject constructor(
    currentBook: CurrentBook,
    val currentChapter: CurrentChapter,
    parser: RoyalRoadParser
) : ViewModel() {

    fun bookReady(book: Book) {
        uiState = BookUiState(book)
    }

    internal var uiState by mutableStateOf(BookUiState())
        private set

    val url = currentBook.book?.uri

    init {
        loadChapters(this, parser.parser)
    }
}

@Composable
fun BookScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: BookModel = hiltViewModel()
) {
    when (viewModel.uiState.book) {
        null -> CenteredLoadingSpinner()
        else -> ChaptersScreen(navController, paddingValues, viewModel.uiState.book!!)
    }
}

private fun loadChapters(viewModel: BookModel, parser: Parser) {
    viewModel.url?.let {
        NetUtil.run(it, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { r ->
                    val book = parser.parseBook(r.string())
                    viewModel.bookReady(book)
                }
            }
        })
    }
}

@Composable
private fun HeaderView(book: Book) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = book.imageUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(128.dp)
                .padding(all = 16.dp)
                .aspectRatio(ratio = 0.75f, matchHeightConstraintsFirst = false)
                .clip(RoundedCornerShape(8.dp))
        )
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = book.title,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(text = book.author, fontWeight = FontWeight.Bold)
        }
    }
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        BookAction(Icons.Outlined.FavoriteBorder, "Add To Library") {
            println("Add " + book.title + " To library pls :)")
        }
    }
    ExpandingText(text = book.description, modifier = Modifier.padding(start = 16.dp, end = 16.dp))
}

@Composable
private fun BookAction(image: ImageVector, description: String, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.clickable { onClick() }) {
        Icon(
            image,
            description,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            tint = Color.Gray
        )
        Text(description, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
private fun ChaptersScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    book: Book,
    viewModel: BookModel = hiltViewModel()
) {
    LazyColumn {
        item {
            Spacer(Modifier.padding(top = paddingValues.calculateTopPadding()))
        }
        item {
            HeaderView(book)
        }
        items(book.chapters) { chapter ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        viewModel.currentChapter.chapter = chapter
                        navController.navigate("read_chapter")
                    }
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        chapter.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text("12/07/2021", color = Color.Gray)
                }
                Icon(
                    Icons.Filled.Download,
                    "Download Chapter",
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            println("Download: ${chapter.title}")
                        }
                        .padding(8.dp))
            }
        }
    }
}

@Composable
fun ExpandingText(modifier: Modifier = Modifier, text: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    var isClickable by remember { mutableStateOf(false) }
    var finalText by remember { mutableStateOf(text) }

    val textLayoutResult = textLayoutResultState.value
    LaunchedEffect(textLayoutResult) {
        if (textLayoutResult == null) return@LaunchedEffect

        when {
            isExpanded -> {
                finalText = text
            }
            !isExpanded && textLayoutResult.hasVisualOverflow -> {
                val lastCharIndex = textLayoutResult.getLineEnd(MINIMIZED_MAX_LINES - 1)
                finalText = text
                    .substring(startIndex = 0, endIndex = lastCharIndex)
                    .dropLastWhile { it == ' ' || it == '.' }


                isClickable = true

            }
        }
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
    ) {
        Text(
            text = finalText,
            maxLines = if (isExpanded) Int.MAX_VALUE else MINIMIZED_MAX_LINES,
            onTextLayout = { textLayoutResultState.value = it },
            modifier = modifier
                .clickable(enabled = isClickable) { isExpanded = !isExpanded }
                .animateContentSize(),
        )
        if (!isExpanded) {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colors.background
                            )
                        )
                    )
            )
            Icon(Icons.Filled.ExpandMore, "Expand")
        }
    }
}
