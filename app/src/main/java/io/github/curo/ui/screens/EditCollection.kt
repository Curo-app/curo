package io.github.curo.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.curo.R
import io.github.curo.data.CollectionName
import io.github.curo.data.CollectionPatchViewModel
import io.github.curo.data.CollectionPreviewModel
import io.github.curo.data.Note
import io.github.curo.data.SwipeDeleteProperties
import io.github.curo.ui.base.*
import io.github.curo.ui.theme.CuroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCollectionScreen(
    viewModel: CollectionPatchViewModel,
    modifier: Modifier = Modifier,
    onNoteClick: (Note) -> Unit,
    onCollectionClick: (CollectionName) -> Unit,
    onAddNoteClick: () -> Unit,
    onDeleteCollectionClick: (CollectionName) -> Unit,
    onShareCollectionClick: () -> Unit,
    onBackToMenuClick: () -> Unit,
    onSaveClick: (CollectionPreviewModel) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(
                title = {
                    BasicTextField(
                        value = viewModel.name,
                        onValueChange = { viewModel.name = it },
                        textStyle = LocalTextStyle.current.copy(
                            color = LocalContentColor.current
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
                    IconButton(onClick = onAddNoteClick) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(R.string.add_note)
                        )
                    }
                    IconButton(onClick = onShareCollectionClick) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.share_collection)
                        )
                    }
                    IconButton(onClick = {
                        onDeleteCollectionClick(
                            CollectionName(viewModel.name)
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(R.string.delete_collection)
                        )
                    }
                    // Maybe add color or eye icons?
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { onSaveClick(viewModel.toCollection()) },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(
                            Icons.Rounded.Done,
                            contentDescription = stringResource(R.string.save_collection)
                        )
                    }
                }
            )
        },
    ) { padding ->
        Feed(
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
    val color by animateColorAsState(
        if (dismissState.targetValue == DismissValue.DismissedToStart) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.surface
        }
    )

    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = SwipeDeleteProperties.alignment
    ) {
        Icon(
            imageVector = SwipeDeleteProperties.icon,
            contentDescription = stringResource(SwipeDeleteProperties.contentDescriptionId),
            modifier = Modifier.scale(scale)
        )
    }
}

@Preview
@Composable
fun EditCollectionPreview() {
    val viewModel by remember { mutableStateOf(CollectionPatchViewModel()) }
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
                onBackToMenuClick = { /* do something */ },
                onSaveClick = { /* do something */ },
            )
        }
    }
}
