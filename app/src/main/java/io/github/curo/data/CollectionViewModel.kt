package io.github.curo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CollectionViewModel : ViewModel() {
    private val itemsList = MutableStateFlow(listOf<CollectionPreviewModel>())
    val items: StateFlow<List<CollectionPreviewModel>> get() = itemsList

    private val itemIdsList = MutableStateFlow(listOf<Int>())
    val itemIds: StateFlow<List<Int>> get() = itemIdsList

    init {
        getFakeData()
    }

    private fun getFakeData() {
        val calendar = Calendar.getInstance().apply {
            time = Date()
        }

        fun Calendar.inc(): Calendar {
            add(Calendar.DATE, 1)
            return this
        }

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
                deadline = Deadline.of(calendar.time),
                emoji = Emoji("\uD83D\uDE02"),
                name = "Не забыть про нюанс",
                collections = listOf("Приколы").map { CollectionName(it) }
            ),
            NotePreviewModel(
                emoji = Emoji("\uD83D\uDE02"),
                name = "Там еще какой-то прикол был...",
                description = "Что-то про еврея, американца и русского",
                collections = listOf("Приколы").map { CollectionName(it) }
            ),
            NotePreviewModel(
                deadline = Deadline.of(calendar.inc().time),
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

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val collections = listOf(
                    CollectionPreviewModel(
                        id = 0,
                        name = "Homework",
                        notes = listOf(notes[5])
                    ),
                    CollectionPreviewModel(
                        id = 1,
                        name = "Shopping list",
                        notes = notes.drop(1)
                    ),
                    CollectionPreviewModel(
                        id = 2,
                        emoji = Emoji("\uD83E\uDD21"),
                        name = "Jokes",
                        notes = listOf(notes[4])
                    ),
                    CollectionPreviewModel(
                        id = 3,
                        emoji = Emoji("\uD83D\uDC7D"),
                        name = "My super list",
                        notes = notes.drop(2)
                    ),
                    CollectionPreviewModel(
                        id = 4,
                        name = "TODO",
                        notes = notes.drop(5)
                    ),
                    CollectionPreviewModel(
                        id = 5,
                        emoji = Emoji("\uD83C\uDF34"),
                        name = "Suuuuuuuuuuuuuuuuuuuper name",
                        notes = notes.drop(6)
                    ),
                    CollectionPreviewModel(
                        id = 6,
                        name = "Suuuuuuuuuuuuuuuuuuuuuuper list",
                        notes = notes.drop(4)
                    ),
                    CollectionPreviewModel(
                        id = 7,
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