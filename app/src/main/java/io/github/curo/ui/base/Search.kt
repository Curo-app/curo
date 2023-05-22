package io.github.curo.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.curo.R

@Composable
fun SearchTopAppBar(
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)?,
    onSearch: () -> Unit = {},
    onMenuClick: () -> Unit = {},
) {
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

        if (content != null) {
            CompactSearch(
                content = content,
                onSearch = { onSearch() }
            )
        } else {
            SearchBar(
                modifier = Modifier
                    .weight(1f),
                onSearch = onSearch,
            )
        }
    }
}

@Composable
fun CompactSearch(
    content: @Composable () -> Unit,
    onSearch: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        content()
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = stringResource(R.string.searchbar_icon_description),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
                .clickable(onClick = onSearch)
        )
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: () -> Unit = {},
) {
    val hint: String = stringResource(R.string.search_hint)

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onSearch() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
    ) {
        Row {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = stringResource(R.string.searchbar_icon_description),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterVertically)
            )
            Box {
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

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    SearchBar()
}
