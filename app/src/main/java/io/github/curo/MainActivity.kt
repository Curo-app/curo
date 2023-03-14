package io.github.curo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import io.github.curo.data.CollectionViewModel
import io.github.curo.ui.base.Collections


class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<CollectionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Collections(viewModel = viewModel)
        }
    }
}

