package io.github.curo.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

@Stable
class CollectionPreviewModel(
    val id: Int,
    val emoji: Emoji = Emoji("\uD83D\uDDC2"),
    val name: String,
    val notes: List<Note>
) {
    val progress: CollectionProgress? by derivedStateOf {
        notes.let { notes ->
            val size = notes.size
            val todoNotes = notes.count { it.done != null }

            if (todoNotes == 0) null
            else CollectionProgress(
                total = size,
                done = size - notes.count { it.done == false }
            )
        }
    }
}

@Immutable
data class CollectionProgress(val total: Int, val done: Int) {
    override fun toString(): String {
        return "$done/$total"
    }

    fun isFinished(): Boolean = total== done
}

