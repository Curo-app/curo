package io.github.curo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.curo.R
import io.github.curo.data.CollectionViewModel
import io.github.curo.ui.base.*
import io.github.curo.ui.theme.CuroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCollectionScreen(modifier: Modifier = Modifier, viewModel: CollectionViewModel) {
    val collectionPreviewModel = viewModel.items
    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(
                title = { Text(text = collectionPreviewModel.value[1].name) },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back_to_collections),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.edit_collection),
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .background(color = MaterialTheme.colorScheme.background)
                .wrapContentSize()
        ) {
            itemsIndexed(collectionPreviewModel.value[1].notes) { _, item ->
                NoteCard(
                    item = item,
                    onNoteClick = {},
                    onCollectionClick = null,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditCollectionScreenPreview() {
    CuroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val viewModel = remember { CollectionViewModel() }
            EditCollectionScreen(viewModel = viewModel)
        }
    }
}