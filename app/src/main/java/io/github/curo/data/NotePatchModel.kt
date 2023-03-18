package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class NotePatchModel {
    var name: String by mutableStateOf("My Note")

    var deadline: Deadline? by mutableStateOf(null)
    var collections = mutableStateListOf<String>()
    var hasCheckbox by mutableStateOf(false)
}