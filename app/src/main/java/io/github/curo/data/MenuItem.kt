package io.github.curo.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class MenuItem(
    val title: String,
    val contentDescription: String,
    val icon: ImageVector
)