package io.github.curo.data

import androidx.compose.runtime.Immutable

@Immutable
data class NotePreviewModel(
    val deadline: Deadline? = null,
    val emoji: Emoji = Emoji("\uD83D\uDCDD"),
    val name: String,
    val description: String? = null,
    val collections: List<CollectionName> = emptyList(),
    val done: Boolean? = null
)

@JvmInline
@Immutable
value class CollectionName(val name: String)
