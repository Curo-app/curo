package io.github.curo.data

import androidx.compose.material.DismissDirection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.curo.R

sealed class SwipeProperties(val alignment: Alignment, val icon: ImageVector, val contentDescriptionId: Int) {
    object Done : SwipeProperties(Alignment.CenterStart, Icons.Default.Done, R.string.done)
    object Delete : SwipeProperties(Alignment.CenterEnd, Icons.Default.Delete, R.string.delete)

    companion object {
        fun of(dismissDirection: DismissDirection): SwipeProperties {
            return when (dismissDirection) {
                DismissDirection.StartToEnd -> Done
                DismissDirection.EndToStart -> Delete
            }
        }
    }
}