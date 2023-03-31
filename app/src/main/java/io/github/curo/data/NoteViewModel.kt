package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.random.Random

@Stable
class NoteViewModel : ViewModel() {
    private val itemsList = MutableStateFlow(listOf<Note>())
    val items: StateFlow<List<Note>> get() = itemsList
    val patchItem = items.map { items ->
        val item = items.first()
        NotePatch().apply {
            name = item.name
            collections.addAll(item.collections)
            deadline = item.deadline
            hasCheckbox = item.done != null
        }
    }

    init {
        getFakeData()
    }

    private fun getFakeData() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val today = LocalDate.now()

                val notes = listOf(
                    Note(
                        id = Random.nextInt(),
                        name = "My first notedddddddddddddddddddddddddddfffffffffffffffff",
                        description = "My note descriptiondsdddddddddddddddddddddddddffffffffffffffffff",
                    ),
                    Note(
                        id = Random.nextInt(),
                        emoji = Emoji("\uD83D\uDE3F"),
                        name = "Забыть матан",
                        done = false
                    ),
                    Note(
                        id = Random.nextInt(),
                        emoji = Emoji("\uD83D\uDE13"),
                        name = "Something",
                        description = "Buy milk",
                        done = false
                    ),
                    Note(
                        id = Random.nextInt(),
                        deadline = Deadline.of(today),
                        emoji = Emoji("\uD83D\uDE02"),
                        name = "Не забыть про нюанс",
                        collections = listOf("Приколы").map { CollectionName(it) },
                        done = true
                    ),
                    Note(
                        id = Random.nextInt(),
                        emoji = Emoji("\uD83D\uDE02"),
                        name = "Там еще какой-то прикол был...",
                        description = "Что-то про еврея, американца и русского",
                        collections = listOf("Приколы").map { CollectionName(it) }
                    ),
                    Note(
                        id = Random.nextInt(),
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
                    Note(
                        id = Random.nextInt(),
                        name = "Отжаться 21 раз",
                        done = true
                    ),
                    Note(
                        id = Random.nextInt(),
                        name = "Отжаться 22 раз",
                        done = true
                    ),
                    Note(
                        id = Random.nextInt(),
                        name = "Отжаться 23 раз",
                        done = true
                    ),
                    Note(
                        id = Random.nextInt(),
                        name = "Отжаться 24 раз",
                        done = true
                    )
                )
                itemsList.emit(notes)
            }
        }
    }
}