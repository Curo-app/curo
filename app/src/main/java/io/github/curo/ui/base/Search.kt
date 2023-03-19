package io.github.curo.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.github.curo.R

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
) {
    val hint: String = stringResource(R.string.search_hint)
    var text by remember { mutableStateOf("") }
    var isHint by remember { mutableStateOf(hint.isNotEmpty()) }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primary),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
    ) {
        Row() {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = stringResource(R.string.searchbar_icon_description),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterVertically)
            )
            Box() {
                BasicTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        onSearch(it)
                    },
                    maxLines = 1,
                    singleLine = true,
                    modifier = Modifier
                        .padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
                        .onFocusChanged { isHint = !it.isFocused },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    )
                )
                if (isHint) {
                    Text(
                        text = hint,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
