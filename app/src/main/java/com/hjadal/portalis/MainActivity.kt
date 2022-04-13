package com.hjadal.portalis

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hjadal.portalis.database.SourceItem
import com.hjadal.portalis.database.SourceRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
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
            NavigationBars(navController, topBar = TopBar("")) {
                BookScreen(navController)
            }
        }
        composable("read_chapter") {
            ReadChapter()
        }
        composable("overview") {
            NavigationBars(navController) {
                Overview(navController)
            }
        }
        composable("database") {
            NavigationBars(navController) {
                SourcesView()
            }
        }
    }
}

@HiltViewModel
class SourceViewModel @Inject constructor(
    val repository: SourceRepository
) : ViewModel() {
    fun AddItem (){
        viewModelScope.launch {
            repository.addSource(SourceItem(itemName = "Mester"))
            println("Added new item")
        }
    }

    fun deleteItem(item: SourceItem) {
        viewModelScope.launch {
           repository.deleteSource(item)
        }
    }
}

@Composable
fun SourcesView(sourceViewModel: SourceViewModel = hiltViewModel()) {
    var size by remember { mutableStateOf(-1)}
    val sourceItems by sourceViewModel.repository.readAllData.observeAsState()
    Column {
        Text(text = size.toString())
        Button(onClick = { sourceViewModel.AddItem()
        }) {
            Text("Click me!")
        }
        sourceItems?.let { i ->
            LazyColumn {
                items(i) { item ->
                    Row {
                        Text(text = "${item.itemId} ${item.itemName}")
                        Button(onClick = {sourceViewModel.deleteItem(item)}) {
                            Text("Delete me")
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen(val route: String, val display: String, val icon: ImageVector) {
    object Overview : Screen("overview", "Library", Icons.Filled.CollectionsBookmark)
    object Database : Screen("database", "Browse", Icons.Filled.Explore)
}


private fun TopBar(title: String = "Portalis"): @Composable (NavHostController, MutableState<Float>) -> Unit {
    return (@Composable { navController, barOffsetHeightPx ->
        TopAppBar(
            modifier = Modifier
                .offset { IntOffset(x = 0, y = barOffsetHeightPx.value.roundToInt()) },
            title = { Text(title) },
            navigationIcon = if (navController.previousBackStackEntry != null) {
                {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            } else {
                null
            }
        )
    })
}

@Composable
private fun NavigationBars(
    navController: NavHostController,
    topBar: @Composable (NavHostController, MutableState<Float>) -> Unit = TopBar(),
    content: @Composable (PaddingValues) -> Unit
) {
    val bottomBarHeight = 60.dp
    val bottomBarHeightPx = with(LocalDensity.current) { bottomBarHeight.roundToPx().toFloat() }
    val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }

    // https://stackoverflow.com/questions/67727773/collapse-navigation-bottombar-while-scrolling-on-jetpack-compose
    // connection to the nested scroll system and listen to the scroll
    // happening inside child LazyColumn
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

                val delta = available.y
                val newOffset = bottomBarOffsetHeightPx.value + delta
                bottomBarOffsetHeightPx.value = newOffset.coerceIn(-bottomBarHeightPx, 0f)

                return Offset.Zero
            }
        }
    }

    Scaffold(
        Modifier.nestedScroll(nestedScrollConnection),
        topBar = { topBar(navController, bottomBarOffsetHeightPx) },
        content = content,
        bottomBar = {
            BottomNavigation(
                modifier = Modifier
                    .offset { IntOffset(x = 0, y = -bottomBarOffsetHeightPx.value.roundToInt()) }
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                listOf(
                    Screen.Overview,
                    Screen.Database,
                ).forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = screen.display) },
                        label = { Text(screen.display) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    )
}