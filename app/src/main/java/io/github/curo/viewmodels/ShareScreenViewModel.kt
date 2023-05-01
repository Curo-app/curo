package io.github.curo.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ShareScreenViewModel : ViewModel() {
    var link : String? by mutableStateOf(null)
}