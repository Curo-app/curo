package io.github.curo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.curo.ui.MainScreen
import io.github.curo.ui.theme.CuroTheme
import androidx.activity.viewModels
import io.github.curo.data.CollectionViewModel
import io.github.curo.ui.base.Collections

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<CollectionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CuroTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
            
            // Collections(viewModel = viewModel) // Maybe move to ui/screens?
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    CuroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen()
        }
    }
}
