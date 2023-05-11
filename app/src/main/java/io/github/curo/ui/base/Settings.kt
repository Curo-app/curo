package io.github.curo.ui.base

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.curo.R
import io.github.curo.data.Emoji
import io.github.curo.viewmodels.ThemeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(themeViewModel: ThemeViewModel, drawerState: DrawerState, scope: CoroutineScope) {
    Scaffold(
        topBar = {
            TextTopAppBar(
                text = stringResource(id = R.string.settings_screen_name),
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        content = { padding ->
            SettingsContent(themeViewModel, Modifier.padding(padding))
        }
    )
}

@Composable
private fun SettingsContent(themeViewModel: ThemeViewModel, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.padding(horizontal = 30.dp, vertical = 5.dp)) {
        Column(Modifier.padding(5.dp)) {
            SelectSettingItem(
                themeViewModel = themeViewModel,
                suggestions = listOf(
                    (stringResource(id = R.string.light_theme) to false),
                    (stringResource(id = R.string.dark_theme) to true)
                ),
                label = stringResource(id = R.string.theme_label),
                contentDescription = stringResource(id = R.string.theme_content_description)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSettingItem(
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
    suggestions: List<Pair<String, Boolean>>,
    label: String,
    contentDescription: String
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }

    val icon =
        if (expanded) Icons.Rounded.KeyboardArrowUp
        else Icons.Rounded.KeyboardArrowDown


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            modifier = modifier
                .width(330.dp)
                .menuAnchor(),
            label = { Text(label) },
            trailingIcon = {
                Icon(icon, contentDescription)
            },
            singleLine = true,
            readOnly = true,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            suggestions.forEach { (label, isDark) ->
                DropdownMenuItem(
                    text = { Text(text = label) },
                    onClick = {
                        selectedText = label
                        themeViewModel.onThemeChanged(isDark)
                        expanded = false
                    }
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUs(drawerState: DrawerState, scope: CoroutineScope) {
    Scaffold(
        topBar = {
            TextTopAppBar(
                text = stringResource(id = R.string.about_us_screen_name),
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        content = { padding ->
            AboutUsContent(
                Modifier.padding(padding)
            )
        }
    )
}

@Composable
private fun AboutUsContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 30.dp, vertical = 5.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { EmojiContainer(Emoji(stringResource(id = R.string.main_emoji)), size = 200F) }
        item {
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Text(
                modifier = modifier.padding(horizontal = 1.dp, vertical = 20.dp),
                text = stringResource(id = R.string.about_us_text),
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun TextTopAppBar(modifier: Modifier = Modifier, text: String, onMenuClick: () -> Unit = {}) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = stringResource(R.string.topappbar_menu_icon_description),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 25.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun AboutUsPreview() {
    AboutUsContent()
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SettingsContent(viewModel())
}