package io.github.curo.data

import androidx.compose.runtime.Immutable
import io.github.curo.R

@Immutable
sealed class FABMenuItem(
    val id: Int,
) {
    @Immutable
    object None : FABMenuItem(-1)
    @Immutable
    object ShoppingList : FABMenuItem(R.string.shopping_list)
    @Immutable
    object TODOList : FABMenuItem(R.string.todolist)
    @Immutable
    object Note : FABMenuItem(R.string.note)
}