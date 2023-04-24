package io.github.curo.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.curo.R

object SwipeDeleteProperties {
    val alignment: Alignment = Alignment.CenterEnd
    val icon: ImageVector = Icons.Default.Delete
    val contentDescriptionId = R.string.delete
}