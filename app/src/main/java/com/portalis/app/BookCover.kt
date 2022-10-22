package com.portalis.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.portalis.app.BitmapImageSource
import com.portalis.app.ImageSource
import com.portalis.lib.NetUtil
import com.portalis.app.UrlImageSource

@Composable
fun BookCover(title: String, image: ImageSource?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
            .aspectRatio(ratio = 0.75f, matchHeightConstraintsFirst = false)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomStart,
    ) {
        if (image != null) {
            AsyncImage(
                model = image.getImage(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(128.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black
                        )
                    )
                )
        )
        Text(
            text = title,
            modifier = Modifier.padding(8.dp),
            color = Color.White,
            fontSize = 16.sp,
        )
    }
}

@Preview
@Composable
fun URLPreview() {
    BookCover(
        "Book Url",
        UrlImageSource("https://avatars.githubusercontent.com/u/5580160?s=88&u=50d7cf1469ca57370cb7e9a24653e64da43cf0ca&v=4")
    ) {
    }
}

@Preview
@Composable
fun NullPreview() {
    BookCover("Book Null", null) {}
}