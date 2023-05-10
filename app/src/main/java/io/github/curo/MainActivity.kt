package io.github.curo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import io.github.curo.ui.AppScreen
import io.github.curo.ui.theme.CuroTheme
import io.github.curo.viewmodels.CalendarViewModel
import io.github.curo.viewmodels.CollectionPatchViewModel
import io.github.curo.viewmodels.CollectionViewModel
import io.github.curo.viewmodels.FeedViewModel
import io.github.curo.viewmodels.NotePatchViewModel
import io.github.curo.viewmodels.NoteViewModel
import io.github.curo.viewmodels.RealCollectionViewModel
import io.github.curo.viewmodels.SearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteViewModel: NoteViewModel by viewModels {
            val database = (application as CuroApplication).database
            NoteViewModel.NoteViewModelFactory(
                database.noteDao()
            )
        }
        val notePatchViewModel: NotePatchViewModel by viewModels {
            val database = (application as CuroApplication).database
            NotePatchViewModel.NotePatchViewModelFactory(
                database.noteDao(),
                database.collectionDao(),
                database.noteCollectionCrossRefDao()
            )
        }
        val realCollectionViewModel: RealCollectionViewModel by viewModels {
            val database = (application as CuroApplication).database
            RealCollectionViewModel.RealCollectionViewModelFactory(
                database.noteDao(),
                database.collectionDao(),
                database.noteCollectionCrossRefDao()
            )
        }
        val collectionPatchViewModel: CollectionPatchViewModel by viewModels {
            val database = (application as CuroApplication).database
            CollectionPatchViewModel.CollectionPatchViewModelFactory(
                database.noteDao(),
                database.collectionDao(),
                database.noteCollectionCrossRefDao()
            )
        }
        val feedViewModel: FeedViewModel by viewModels {
            val database = (application as CuroApplication).database
            FeedViewModel.FeedViewModelFactory(
                database.noteDao()
            )
        }
        val calendarViewModel: CalendarViewModel by viewModels {
            val database = (application as CuroApplication).database
            CalendarViewModel.CalendarViewModelFactory(
                database.noteDao()
            )
        }
        val collectionViewModel: CollectionViewModel by viewModels {
            val database = (application as CuroApplication).database
            CollectionViewModel.CollectionViewModelFactory(
                database.noteDao(),
                database.collectionDao(),
                database.noteCollectionCrossRefDao()
            )
        }
        val searchViewModel: SearchViewModel by viewModels {
            val database = (application as CuroApplication).database
            SearchViewModel.SearchViewModelFactory(
                database.noteDao()
            )
        }
        setContent {
            CuroTheme {
                AppScreen(
                    feedViewModel,
                    noteViewModel,
                    notePatchViewModel,
                    realCollectionViewModel,
                    collectionPatchViewModel,
                    calendarViewModel,
                    collectionViewModel,
                    searchViewModel
                )
            }
        }
    }
}
