package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Stable
class CollectionViewModel : ViewModel() {
    private val itemsList = MutableStateFlow(listOf<CollectionPreviewModel>())
    val items: StateFlow<List<CollectionPreviewModel>> get() = itemsList

    private val itemIdsList = MutableStateFlow(listOf<Int>())
    val itemIds: StateFlow<List<Int>> get() = itemIdsList

    var searchQuery by mutableStateOf("")
    val searchResult by derivedStateOf {
        itemsList.value.filter {
            it.name.contains(searchQuery, true)
        }.map { it.name }
    }


    init {
        getFakeData()
    }

    private fun getFakeData() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val today = LocalDate.now()

                val notes = listOf(
                    NotePreviewModel(
                        name = "My first notedddddddddddddddddddddddddddfffffffffffffffff",
                        description = "My note descriptiondsdddddddddddddddddddddddddffffffffffffffffff",
                    ),
                    NotePreviewModel(
                        emoji = Emoji("\uD83D\uDE3F"),
                        name = "Забыть матан",
                        done = false
                    ),
                    NotePreviewModel(
                        emoji = Emoji("\uD83D\uDE13"),
                        name = "Something",
                        description = "Buy milk",
                        done = false
                    ),
                    NotePreviewModel(
                        deadline = Deadline.of(today),
                        emoji = Emoji("\uD83D\uDE02"),
                        name = "Не забыть про нюанс",
                        collections = listOf("Приколы").map { CollectionName(it) },
                        done = true,
                    ),
                    NotePreviewModel(
                        emoji = Emoji("\uD83D\uDE02"),
                        name = "Там еще какой-то прикол был...",
                        description = "Что-то про еврея, американца и русского",
                        collections = listOf("Приколы").map { CollectionName(it) }
                    ),
                    NotePreviewModel(
                        deadline = Deadline.of(today.plusDays(1)),
                        emoji = Emoji("\uD83D\uDC7D"),
                        name = "FP HW 3",
                        description = "Надо быстрее сделать",
                        collections = listOf(
                            "Домашка",
                            "Важное",
                            "Haskell",
                            "Ненавижу ФП"
                        ).map { CollectionName(it) },
                        done = true
                    ),
                    NotePreviewModel(
                        name = "Отжаться 21 раз",
                        done = true
                    )
                )
                val collections = listOf(
                    CollectionPreviewModel(
                        name = "Homework",
                        notes = listOf(notes[5])
                    ),
                    CollectionPreviewModel(
                        name = "Shopping list",
                        notes = notes.drop(1)
                    ),
                    CollectionPreviewModel(
                        emoji = Emoji("\uD83E\uDD21"),
                        name = "Jokes",
                        notes = listOf(notes[4])
                    ),
                    CollectionPreviewModel(
                        emoji = Emoji("\uD83D\uDC7D"),
                        name = "My super list",
                        notes = notes.drop(2)
                    ),
                    CollectionPreviewModel(
                        name = "TODO",
                        notes = notes.drop(5)
                    ),
                    CollectionPreviewModel(
                        emoji = Emoji("\uD83C\uDF34"),
                        name = "Suuuuuuuuuuuuuuuuuuuper name",
                        notes = notes.drop(6)
                    ),
                    CollectionPreviewModel(
                        name = "Suuuuuuuuuuuuuuuuuuuuuuper list",
                        notes = notes.drop(4)
                    ),
                    CollectionPreviewModel(
                        name = "Suuuuuuuuuuuuuuuuuuuuuuuuper list 22222",
                        notes = notes
                    ),
                )
                itemsList.emit(collections)
            }
        }
    }

    fun onItemClicked(cardId: Int) {
        itemIdsList.value = itemIdsList.value.toMutableList().also { list ->
            if (list.contains(cardId)) list.remove(cardId) else list.add(cardId)
        }
    }
}