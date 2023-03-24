package io.github.curo.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.material.DropdownMenu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import io.github.curo.R
import io.github.curo.data.Emoji
import kotlinx.coroutines.launch

@Composable
fun Settings(modifier: Modifier = Modifier) {
    NavigationMenu(
        modifier = modifier,
        topBar = { scope, scaffoldState ->
            TextTopAppBar(
                text = stringResource(id = R.string.settings_screen_name),
                onMenuClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        content = { contentModifier, innerPadding ->
            SettingsContent(modifier = contentModifier.padding(innerPadding))
        })
}

@Composable
fun SettingsContent(modifier: Modifier = Modifier) {
    Surface(modifier = modifier.padding(horizontal = 30.dp, vertical = 5.dp)) {
        Column(modifier.padding(5.dp)) {
            SelectSettingItem(
                modifier = modifier,
                suggestions = listOf(
                    stringResource(id = R.string.russian_lang),
                    stringResource(id = R.string.english_lang)
                ),
                label = stringResource(id = R.string.lang_label),
                contentDescription = stringResource(id = R.string.lang_content_description)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSettingItem(
    modifier: Modifier = Modifier,
    suggestions: List<String>,
    label: String,
    contentDescription: String
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }

    val icon =
        if (expanded) Icons.Rounded.KeyboardArrowUp
        else Icons.Rounded.KeyboardArrowDown


    Box {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            modifier = modifier.width(330.dp),
            label = { Text(label) },
            trailingIcon = {
                Icon(icon, contentDescription, modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier.width(330.dp)
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(
                    text = { Text(text = label) },
                    onClick = {
                        selectedText = label
                        expanded = false
                    })
            }
        }
    }

}

@Composable
fun AboutUs(modifier: Modifier = Modifier) {
    NavigationMenu(
        modifier = modifier,
        topBar = { scope, scaffoldState ->
            TextTopAppBar(
                text = stringResource(id = R.string.about_us),
                onMenuClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        content = { contentModifier, innerPadding ->
            AboutUsContent(contentModifier.padding(innerPadding))
        })
}

@Composable
fun AboutUsContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 30.dp, vertical = 5.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmojiContainer(Emoji(stringResource(id = R.string.main_emoji)), size = 200F)
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = modifier.padding(horizontal = 1.dp, vertical = 20.dp),
            text = stringResource(id = R.string.about_us_text),
            fontSize = 18.sp
        )
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
    AboutUs()
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    Settings()
}