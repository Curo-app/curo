package io.github.curo.data

import androidx.compose.runtime.Immutable

@Immutable
data class Note(
    val id: Int,
    val title: String,
    val content: String
)