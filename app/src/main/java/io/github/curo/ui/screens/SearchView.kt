package io.github.curo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.curo.R
import io.github.curo.data.NotePreview
import io.github.curo.database.entities.CollectionInfo
import io.github.curo.viewmodels.SearchViewModel
import io.github.curo.ui.base.Feed

@Composable
fun SearchView(
    onSearchTextChanged: (String) -> Unit,
    onSearchKeyboardClick: () -> Unit,
    onLeadingIconClick: () -> Unit,
    searchViewModel: SearchViewModel,
    onNoteClick: (NotePreview) -> Unit,
    onCollectionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
                IconButton(
                    onClick = { onLeadingIconClick() },
                ) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back button")
                }
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = searchViewModel.query,
                        onValueChange = {
                            searchViewModel.query = it
                            onSearchTextChanged(it)
                        },
                        maxLines = 1,
                        singleLine = true,
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { onSearchKeyboardClick() }
                        )
                    )
                    if (searchViewModel.query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_hint),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                if (searchViewModel.query.isNotEmpty()) {
                    IconButton(onClick = { searchViewModel.query = "" }) {
                        Icon(
                            Icons.Rounded.Clear,
                            contentDescription = stringResource(R.string.clear_input)
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

        if (searchViewModel.notes.isEmpty()) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    modifier = Modifier.size(128.dp),
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.inversePrimary,
                )
                Text(text = "Nothing found")
            }
        } else {
            Feed(
                onNoteClick = onNoteClick,
                onCollectionClick = onCollectionClick,
                viewModel = searchViewModel
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {
    MaterialTheme {
        SearchView(
            onSearchTextChanged = { /* Updated each time the text changes */ },
            onSearchKeyboardClick = { /* TODO: Show results or navigate to separate screen */ },
            onLeadingIconClick = { /* TODO: Navigate to previous screen */ },
            searchViewModel = remember { SearchViewModel() },
            onNoteClick = { /* TODO */ },
            onCollectionClick = { /* TODO */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchViewPreviewResults() {
    MaterialTheme {
        SearchView(
            onSearchTextChanged = { /* Updated each time the text changes */ },
            onSearchKeyboardClick = { /* TODO: Show results or navigate to separate screen */ },
            onLeadingIconClick = { /* TODO: Navigate to previous screen */ },
            searchViewModel = remember { SearchViewModel().apply { query = "ddd" } },
            onNoteClick = { /* TODO */ },
            onCollectionClick = { /* TODO */ }
        )
    }
}

