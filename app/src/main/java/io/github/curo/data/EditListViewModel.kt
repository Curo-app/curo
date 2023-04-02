package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

@Stable
class Collection(
    var name: String,
    val notes: MutableList<Note>
)

class EditListViewModel : ViewModel() {
    private val collection = Collection("Jokes", mutableStateListOf(
        Note(
            id = 0,
            name = "My first notedddddddddddddddddddddddddddfffffffffffffffff",
            description = "My note descriptiondsdddddddddddddddddddddddddffffffffffffffffff",
        ),
        Note(
            id = 1,
            emoji = Emoji("\uD83D\uDE3F"),
            name = "Забыть матан",
            done = false,
            deadline = Deadline.of(LocalDate.now())
        ),
        Note(
            id = 2,
            emoji = Emoji("\uD83D\uDE13"),
            name = "Something",
            description = "Buy milk",
            done = false
        ),
        Note(
            id = 3,
            emoji = Emoji("\uD83D\uDE02"),
            name = "Там еще какой-то прикол был...",
            description = "Что-то про еврея, американца и русского",
            collections = listOf("Приколы").map { CollectionName(it) }
        ),
        Note(
            id = 4,
            name = "Отжаться 21 раз",
            done = true
        )
    ))

    private val _collectionFlow = MutableStateFlow(collection)

    val collectionFlow: MutableStateFlow<Collection> get() = _collectionFlow

    fun removeRecord(editItemPreviewModel: Note) {
        val index = collection.notes.indexOf(editItemPreviewModel)
        collection.notes.remove(collection.notes[index])
    }
}
