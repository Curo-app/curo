package io.github.curo.http

import com.google.gson.annotations.SerializedName
import io.github.curo.data.CollectionPreview
import io.github.curo.data.Deadline
import io.github.curo.data.Emoji
import io.github.curo.data.NotePreview

data class UrlDto(
    @SerializedName("url")
    val url: String,
)

data class NoteDto(
    val id: Long,
    val deadline: Deadline? = null,
    val emoji: Emoji = Emoji("\uD83D\uDCDD"),
    val name: String,
    val description: String?,
    val collections: List<String>,
)

data class CollectionDto(
    val id: Long,
    val emoji: Emoji = Emoji("\uD83D\uDDC2"),
    val name: String,
    val notes: List<NoteDto>
)

fun NotePreview.toDto() = NoteDto(
    id = id,
    deadline = deadline,
    emoji = emoji,
    name = name,
    description = description,
    collections = collections.map { it.collectionName }
)

fun CollectionPreview.toDto() = CollectionDto(
    id = id,
    emoji = emoji,
    name = name,
    notes = notes.map { it.toDto() }
)