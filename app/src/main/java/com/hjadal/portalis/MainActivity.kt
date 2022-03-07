package com.hjadal.portalis

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            PortalisTheme.Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                   SetupRootNav(navController)
                }
            }
        }
    }
}

@Composable
private fun SetupRootNav(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "overview") {
        composable("book_screen") {
            BookScreen(navController)
        }
        composable("read_chapter") {
            ReadChapter()
        }
        composable("overview") {
            Overview(navController)
        }
    }
}
