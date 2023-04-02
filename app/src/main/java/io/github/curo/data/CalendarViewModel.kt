package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.random.Random

@Stable
class CalendarViewModel : ViewModel() {
    private val notesList = MutableStateFlow(listOf<Note>())
    val notes: StateFlow<List<Note>> get() = notesList

    init {
        getFakeData()
    }

    // TODO: change colors
    private val colors = listOf(
        Color(190, 218, 227),
        Color(196, 233, 218),
        Color(254, 213, 207),
        Color(241, 181, 152),
        Color(211, 199, 230)
    )

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
                    ),
                    Note(
                        id = Random.nextInt(),
                        deadline = Deadline.of(today.plusDays(2)),
                        name = "Buy milk",
                        color = colors.random(),
                    ),
                    Note(
                        id = Random.nextInt(),
                        deadline = Deadline.of(today.plusDays(2)),
                        name = "FP homework",
                        color = colors.random(),
                    ),
                    Note(
                        id = Random.nextInt(),
                        deadline = Deadline.of(today.plusDays(2)),
                        name = "Interview at 14:00 PM at Google office",
                        color = colors.random(),
                    ),
                    Note(
                        id = Random.nextInt(),
                        deadline = Deadline.of(today.plusDays(5)),
                        name = "Buy a new phone for my mom on AliExpress for her birthday",
                        color = colors.random(),
                    ),
                    Note(
                        id = Random.nextInt(),
                        deadline = Deadline.of(today.plusDays(5)),
                        name = "Take a walk with my dog at 18:00 PM at the park",
                        color = colors.random(),
                    ),
                    Note(
                        id = Random.nextInt(),
                        deadline = Deadline.of(today.plusDays(5)),
                        name = "Finish the Curo app by 23:59 PM today",
                        color = colors.random(),
                    ),
                    Note(
                        id = Random.nextInt(),
                        deadline = Deadline.of(today.plusDays(5)),
                        name = "Learn how to use the new Android Studio 2021.3.1 Canary 1",
                        color = colors.random(),
                    ),
                    Note(
                        id = Random.nextInt(),
                        deadline = Deadline.of(today.plusDays(5)),
                        name = "Learn how to use the new Android Studio 2021.3.1 Canary 1",
                        color = colors.random(),
                    ),
                )
                notesList.emit(notes)
            }
        }
    }
}