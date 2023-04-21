package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Stable
open class FeedViewModel : ViewModel() {
    private val _notes = mutableStateListOf<Note>()
    open val notes: List<Note> get() = _notes

    fun update(note: Note) {
        val index = _notes.indexOfFirst { it.id == note.id }
        if (index == -1) {
            _notes.add(note)
        } else {
            _notes[index] = note
        }
    }

    fun findOrCreate(id: Int): Note = notes.find { note ->
        note.id == id
    } ?: Note(
        id = notes.maxOf(Note::id).inc(),
        name = "",
    )

    fun delete(id: Int) {
        _notes.removeIf { it.id == id }
    }

    fun addCollection(collection: CollectionPreviewModel) {
        collection.notes.forEach { note ->
            val newNote = Note(
                id = note.id,
                deadline = note.deadline,
                emoji = note.emoji,
                color = note.color,
                name = note.name,
                description = note.description,
                collections = note.collections + collection.name,
                done = note.done,
            )
            update(newNote)
        }
    }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                _notes.addAll(loadItems())
            }
        }

    }

    protected fun loadItems(): List<Note> {
        val today = LocalDate.now()
        return listOf(
            Note(
                id = 1,
                name = "My first notedddddddddddddddddddddddddddfffffffffffffffff",
                description = "My note descriptiondsdddddddddddddddddddddddddffffffffffffffffff",
            ),
            Note(
                id = 2,
                emoji = Emoji("\uD83D\uDE3F"),
                name = "Забыть матан",
                done = false
            ),
            Note(
                id = 3,
                emoji = Emoji("\uD83D\uDE13"),
                name = "Something",
                description = "Buy milk",
                done = false
            ),
            Note(
                id = 4,
                deadline = Deadline.of(today),
                emoji = Emoji("\uD83D\uDE02"),
                name = "Не забыть про нюанс",
                collections = listOf("Приколы"),
                done = false
            ),
            Note(
                id = 5,
                emoji = Emoji("\uD83D\uDE02"),
                name = "Там еще какой-то прикол был...",
                description = "Что-то про еврея, американца и русского",
                collections = listOf("Приколы")
            ),
            Note(
                id = 6,
                deadline = Deadline.of(today.plusDays(1)),
                emoji = Emoji("\uD83D\uDC7D"),
                name = "FP HW 3",
                description = "Надо быстрее сделать",
                collections = listOf(
                    "Домашка",
                    "Важное",
                    "Haskell",
                    "Ненавижу ФП"
                ),
                done = false
            ),
            Note(
                id = 7,
                name = "Отжаться 21 раз",
                done = true
            ),
            Note(
                id = 8,
                name = "Отжаться 22 раз",
                done = true
            ),
            Note(
                id = 9,
                name = "Отжаться 23 раз",
                done = true
            ),
            Note(
                id = 10,
                name = "Отжаться 24 раз",
                done = true
            )
        )
    }
}