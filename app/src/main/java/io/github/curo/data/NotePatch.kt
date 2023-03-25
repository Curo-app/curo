package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class NotePatch(
    val id: Int = 0,
) {
    var name: String by mutableStateOf("My Note")
    var content: String by mutableStateOf("")

    var deadline: Deadline? by mutableStateOf(null)
    var collections = mutableStateListOf<CollectionName>()
    var hasCheckbox by mutableStateOf(false)
}