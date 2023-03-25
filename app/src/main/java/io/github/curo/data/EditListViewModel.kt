package io.github.curo.data

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class EditListViewModel : ViewModel() {
    private var editList = mutableStateListOf(
        NotePreviewModel(
            id = 0,
            name = "My first notedddddddddddddddddddddddddddfffffffffffffffff",
            description = "My note descriptiondsdddddddddddddddddddddddddffffffffffffffffff",
        ),
        NotePreviewModel(
            id = 1,
            emoji = Emoji("\uD83D\uDE3F"),
            name = "Забыть матан",
            done = false,
            deadline = Deadline.of(Date())
        ),
        NotePreviewModel(
            id = 2,
            emoji = Emoji("\uD83D\uDE13"),
            name = "Something",
            description = "Buy milk",
            done = false
        ),
        NotePreviewModel(
            id = 3,
            emoji = Emoji("\uD83D\uDE02"),
            name = "Там еще какой-то прикол был...",
            description = "Что-то про еврея, американца и русского",
            collections = listOf("Приколы").map { CollectionName(it) }
        ),
        NotePreviewModel(
            id = 4,
            name = "Отжаться 21 раз",
            done = true
        )
    )

    private val _editListFlow = MutableStateFlow(editList)

    val editListFlow: StateFlow<List<NotePreviewModel>> get() = _editListFlow

    fun removeRecord(editItemPreviewModel: NotePreviewModel) {
        val index = editList.indexOf(editItemPreviewModel)
        editList.remove(editList[index])
    }
}
