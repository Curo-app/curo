package io.github.curo.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.curo.R
import io.github.curo.data.CollectionName
import io.github.curo.data.EditListViewModel
import io.github.curo.data.Note
import io.github.curo.data.SwipeProperties
import io.github.curo.ui.base.*
import io.github.curo.ui.theme.CuroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCollectionScreen(
    viewModel: EditListViewModel,
    modifier: Modifier = Modifier,
    onNoteClick: (Note) -> Unit,
    onCollectionClick: (CollectionName) -> Unit,
    onAddNoteClick: () -> Unit,
    onDeleteCollectionClick: () -> Unit,
    onShareCollectionClick: () -> Unit,
    onBackToMenuClick: () -> Unit
) {
    val collectionFlow = viewModel.collectionFlow.collectAsState()
    var text by remember { mutableStateOf(collectionFlow.value.name) }
    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(
                title = {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                            color = androidx.compose.material3.LocalContentColor.current
                        ), // workaround for https://stackoverflow.com/questions/73700656/why-is-mediumtopappbar-and-large-showing-two-textfield-in-compose
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToMenuClick) {
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
                    IconButton(onClick = onDeleteCollectionClick) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(R.string.delete_collection)
                        )
                    }
                    IconButton(onClick = onShareCollectionClick) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.share_collection)
                        )
                    }
                    // Maybe add color or eye icons?
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onAddNoteClick,
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
        EditItems(
            viewModel = viewModel,
            modifier = modifier.padding(padding),
            onNoteClick = onNoteClick,
            onCollectionClick = onCollectionClick
        )
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun SwipeBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> MaterialTheme.colorScheme.surface
            DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.primary
            DismissValue.DismissedToStart -> MaterialTheme.colorScheme.error
        }
    )

    val swipeProperties = SwipeProperties.of(direction)

    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = swipeProperties.alignment
    ) {
        Icon(
            imageVector = swipeProperties.icon,
            contentDescription = stringResource(swipeProperties.contentDescriptionId),
            modifier = Modifier.scale(scale)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun EditItems(
    viewModel: EditListViewModel,
    modifier: Modifier = Modifier,
    onNoteClick: (Note) -> Unit,
    onCollectionClick: ((CollectionName) -> Unit)?
) {
    val lazyListState = rememberLazyListState()
    val editListState = viewModel.collectionFlow.collectAsState()
    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        state = lazyListState
    ) {
        items(
            items = editListState.value.notes,
            key = { editItem -> editItem.id }
        ) { item ->
            val currentItem by rememberUpdatedState(item)
            val dismissState = rememberDismissState(
                confirmStateChange = {
                    if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                        viewModel.removeRecord(currentItem)
                        true
                    } else false
                }
            )

            if (dismissState.isDismissed(DismissDirection.EndToStart) ||
                dismissState.isDismissed(DismissDirection.StartToEnd)
            ) {
                viewModel.removeRecord(item)
            }

            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier
                    .padding(vertical = 1.dp)
                    .animateItemPlacement(),
                directions = setOf(
                    DismissDirection.StartToEnd,
                    DismissDirection.EndToStart
                ),
                dismissThresholds = { direction ->
                    androidx.compose.material.FractionalThreshold(
                        if (direction == DismissDirection.StartToEnd) 0.33f else 0.20f
                    )
                },
                background = {
                    SwipeBackground(dismissState)
                },
                dismissContent = {
                    NoteCard(item, onNoteClick, onCollectionClick)
                }
            )
        }
    }
}

@Preview
@Composable
fun EditCollectionPreview() {
    val viewModel by remember { mutableStateOf(EditListViewModel()) }
    CuroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            EditCollectionScreen(
                viewModel = viewModel,
                onNoteClick = { /* TODO */ },
                onCollectionClick = { /* TODO */ },
                onAddNoteClick = { /* do something */ },
                onDeleteCollectionClick = { /* do something */ },
                onShareCollectionClick = { /* do something */ },
                onBackToMenuClick = { /* do something */ }
            )
        }
    }
}
