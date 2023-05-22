package io.github.curo.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.curo.R
import io.github.curo.data.CollectionPreview
import io.github.curo.data.NotePreview
import io.github.curo.database.entities.CollectionInfo
import io.github.curo.ui.base.*
import io.github.curo.viewmodels.CollectionPatchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCollectionScreen(
    collectionPatchViewModel: CollectionPatchViewModel,
    modifier: Modifier = Modifier,
    onNoteClick: (NotePreview) -> Unit,
    onChecked: (NotePreview) -> Unit,
    onAddNote: () -> Unit,
    onDeleteCollection: (CollectionInfo) -> Unit,
    onShareCollection: (CollectionPreview) -> Unit,
    onBackToMenu: () -> Unit,
    onSaveCollection: (CollectionPreview) -> Unit,
) {
    val collectionState by collectionPatchViewModel.collectionPatchUiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(
                title = {
                    TransparentHintTextField(
                        hint = stringResource(R.string.collection_name_hint),
                        text = collectionPatchViewModel.name,
                        onValueChange = { collectionPatchViewModel.name = it },
                        textStyle = LocalTextStyle.current.copy(
                            color = LocalContentColor.current
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToMenu) {
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
                    IconButton(onClick = onAddNote) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(R.string.add_note)
                        )
                    }
                    IconButton(
                        onClick = { onShareCollection(collectionPatchViewModel.toCollectionPreview()) },
                        content = {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = stringResource(R.string.share_collection)
                            )
                        },
                    )
                    IconButton(onClick = {
                        onDeleteCollection(CollectionInfo(collectionPatchViewModel.id, collectionPatchViewModel.name))
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
                        onClick = { onSaveCollection(collectionPatchViewModel.toCollectionPreview()) },
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
        FeedForced(
            modifier = modifier.padding(padding),
            content = collectionState.notes,
            onNoteClick = onNoteClick,
            onChecked = onChecked,
        )
    }
}

//@Preview
//@Composable
//fun EditCollectionPreview() {
//    val viewModel by remember { mutableStateOf(CollectionPatchViewModel()) }
//    CuroTheme {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            EditCollectionScreen(
//                viewModel = viewModel,
//                onNoteClick = { /* TODO */ },
//                onCollectionClick = { /* TODO */ },
//                onAddNoteClick = { /* do something */ },
//                onDeleteCollectionClick = { /* do something */ },
//                onShareCollectionClick = { /* do something */ },
//                onBackToMenuClick = { /* do something */ },
//                onSaveClick = { /* do something */ },
//            )
//        }
//    }
//}
