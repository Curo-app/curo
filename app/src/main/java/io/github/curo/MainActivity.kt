package io.github.curo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.curo.ui.AppScreen
import io.github.curo.ui.theme.CuroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val noteViewModel: NoteViewModel by viewModels {
//            val database = (application as CuroApplication).database
//            NoteViewModel.NoteViewModelFactory(
//                database.noteDao(),
//                database.noteCollectionCrossRefDao()
//            )
//        }
        setContent {
            CuroTheme {
                AppScreen()
            }
        }
    }
}