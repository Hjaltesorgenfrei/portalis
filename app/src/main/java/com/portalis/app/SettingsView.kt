package com.portalis.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.portalis.app.database.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val repository: BookRepository
) : ViewModel() {
    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllBooks()
        }
    }
}

@Composable
fun Settings(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    Column {
        Spacer(modifier = Modifier.padding(top = paddingValues.calculateTopPadding()))
        SettingsAction(text = "Clear Data", icon = Icons.Filled.DeleteForever) {
            viewModel.clearAllData()
        }
        Spacer(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()))
    }
}

@Composable
fun SettingsAction(
    text: String,
    icon: ImageVector? = null,
    onClick : () -> Unit
) {
    SettingsMenuLink(title = { Text(text)}, icon = { icon?.let { Icon(it, text) } }) {
        onClick()
    }
}