package io.github.curo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import io.github.curo.ui.AppScreen
import io.github.curo.ui.theme.CuroTheme
import io.github.curo.viewmodels.NotePatchViewModel
import io.github.curo.viewmodels.NoteViewModel
import io.github.curo.viewmodels.RealCollectionViewModel

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
        setContent {
            CuroTheme {
                AppScreen(noteViewModel, notePatchViewModel, realCollectionViewModel)
            }
        }
    }
}
