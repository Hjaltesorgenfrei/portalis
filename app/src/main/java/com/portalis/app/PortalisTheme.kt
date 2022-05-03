package com.portalis.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object PortalisTheme {
    private val Yellow200 = Color(0xffffeb46)
    private val Blue200 = Color(0xff91a4fc)

    private val DarkColors = darkColors(
    )
    private val LightColors = lightColors(
    )


    @Composable
    fun Theme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
    ) {
        MaterialTheme(
            colors = if (darkTheme) DarkColors else LightColors,
            /*...*/
            content = content
        )
    }

}