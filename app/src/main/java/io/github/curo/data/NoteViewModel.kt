package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Stable
class NoteViewModel : ViewModel() {
    private val itemsList = MutableStateFlow(listOf<NotePreviewModel>())
    val items: StateFlow<List<NotePreviewModel>> get() = itemsList

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
                        done = true
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
                    ),
                    NotePreviewModel(
                        name = "Отжаться 22 раз",
                        done = true
                    ),
                    NotePreviewModel(
                        name = "Отжаться 23 раз",
                        done = true
                    ),
                    NotePreviewModel(
                        name = "Отжаться 24 раз",
                        done = true
                    )
                )
                itemsList.emit(notes)
            }
        }
    }
}