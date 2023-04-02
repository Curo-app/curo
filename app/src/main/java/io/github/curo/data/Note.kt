package io.github.curo.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Stable
class Note(
    val id: Int,
    val deadline: Deadline? = null,
    val emoji: Emoji = Emoji("\uD83D\uDCDD"),
    // TODO: think about nullable property or default color
    val color: Color = Color.Gray,
    val name: String,
    val description: String? = null,
    val collections: List<CollectionName> = emptyList(),
    done: Boolean? = null
) {
    var done by mutableStateOf(done)
}

@JvmInline
@Immutable
value class CollectionName(val name: String)
