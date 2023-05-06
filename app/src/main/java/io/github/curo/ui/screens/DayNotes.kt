package io.github.curo.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.curo.R
import io.github.curo.data.NotePreview
import io.github.curo.ui.base.Feed
import io.github.curo.utils.DateTimeUtils.dateFormatter
import io.github.curo.viewmodels.CalendarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayNotes(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier,
    onNoteClick: (NotePreview) -> Unit,
    onCollectionClick: (String) -> Unit,
    onShareClick: () -> Unit,
    onBackToMenuClick: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = viewModel.currentDay.format(dateFormatter))
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
                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.share_collection)
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
            onCollectionClick = onCollectionClick,
        )
    }
}

//@Preview
//@Composable
//fun DayNotesPreview() {
//    val viewModel = remember { CalendarViewModel() }
//    CuroTheme {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            DayNotes(
//                viewModel = viewModel,
//                onNoteClick = { /* TODO */ },
//                onCollectionClick = { /* TODO */ },
//                onBackToMenuClick = { /* do something */ },
//                onShareClick = {}
//            )
//        }
//    }
//}