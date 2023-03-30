package io.github.curo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.curo.R
import io.github.curo.data.CollectionName
import io.github.curo.data.Deadline
import io.github.curo.data.Emoji
import io.github.curo.data.NotePreviewModel
import io.github.curo.ui.base.NoteCard
import java.util.*

@Composable
fun SearchView(
    onSearchTextChanged: (String) -> Unit,
    onSearchKeyboardClick: () -> Unit,
    onLeadingIconClick: () -> Unit,
    searchResults: List<NotePreviewModel>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val hint: String = stringResource(R.string.search_hint)
                var searchText by remember { mutableStateOf("") }
                var isHint by remember { mutableStateOf(hint.isNotEmpty()) }

                IconButton(
                    onClick = { onLeadingIconClick() },
                ) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back button")
                }
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            onSearchTextChanged(it)
                        },
                        maxLines = 1,
                        singleLine = true,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .onFocusChanged { isHint = !it.isFocused },
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { onSearchKeyboardClick() }
                        )
                    )
                    if (isHint) {
                        Text(
                            text = hint,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { searchText = "" }) {
                        Icon(
                            Icons.Rounded.Clear,
                            contentDescription = stringResource(R.string.clear_search_text)
                        )
                    }
                }
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(searchResults) { item ->
                val modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                NoteCard(modifier = modifier, item, { /* TODO */ }, { /* TODO */ })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {
    val items = listOf(
        NotePreviewModel(
            id = 0,
            name = "My first notedddddddddddddddddddddddddddfffffffffffffffff",
            description = "My note descriptiondsdddddddddddddddddddddddddffffffffffffffffff",
        ),
        NotePreviewModel(
            id = 1,
            emoji = Emoji("\uD83D\uDE3F"),
            name = "Забыть матан",
            done = false,
            deadline = Deadline.of(Date())
        ),
        NotePreviewModel(
            id = 2,
            emoji = Emoji("\uD83D\uDE13"),
            name = "Something",
            description = "Buy milk",
            done = false
        ),
        NotePreviewModel(
            id = 3,
            emoji = Emoji("\uD83D\uDE02"),
            name = "Там еще какой-то прикол был...",
            description = "Что-то про еврея, американца и русского",
            collections = listOf("Приколы").map { CollectionName(it) }
        ),
        NotePreviewModel(
            id = 4,
            name = "Отжаться 21 раз",
            done = true
        )
    )
    MaterialTheme {
        SearchView(
            onSearchTextChanged = { /* Updated each time the text changes */ },
            onLeadingIconClick = { /* TODO: Navigate to previous screen */ },
            onSearchKeyboardClick = { /* TODO: Show results or navigate to separate screen */ },
            searchResults = items
        )
    }
}

