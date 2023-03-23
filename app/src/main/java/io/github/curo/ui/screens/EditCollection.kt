package io.github.curo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.curo.R
import io.github.curo.data.CollectionPreviewModel
import io.github.curo.data.CollectionViewModel
import io.github.curo.ui.base.*
import io.github.curo.ui.theme.CuroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCollectionScreen(modifier: Modifier = Modifier, viewModel: CollectionPreviewModel) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(
                title = { Text(text = viewModel.name) },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back_to_collections)
                        )
                    }
                },
                actions = {}
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.edit_collection)
                        )
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(R.string.delete_collection)
                        )
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.share_collection)
                        )
                    }
                    // Maybe add color or eye icons?
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { /* do something */ },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = stringResource(R.string.add_note)
                        )
                    }
                }
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .background(color = MaterialTheme.colorScheme.background)
                .wrapContentSize()
        ) {
            itemsIndexed(viewModel.notes) { _, item ->
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
fun EditCollectionScreenPreview1() {
    val viewModel by remember { mutableStateOf(CollectionViewModel()) }
    CuroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val items by viewModel.items.collectAsState()
            if (items.isNotEmpty()) {
                EditCollectionScreen(viewModel = items[1])
            } else {
                Text(text = "Unable to preview")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditCollectionScreenPreview2() {
    val viewModel by remember { mutableStateOf(CollectionViewModel()) }
    CuroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val items by viewModel.items.collectAsState()
            if (items.isNotEmpty()) {
                EditCollectionScreen(viewModel = items[2])
            } else {
                Text(text = "Unable to preview")
            }
        }
    }
}
