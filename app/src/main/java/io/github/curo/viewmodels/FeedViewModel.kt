package io.github.curo.viewmodels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.curo.data.CollectionPreview
import io.github.curo.data.Deadline
import io.github.curo.data.Emoji
import io.github.curo.data.NotePreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Stable
open class FeedViewModel : ViewModel() {
    private val _notes = mutableStateListOf<NotePreview>()
    open val notes: List<NotePreview> get() = _notes

    fun update(note: NotePreview) {
        val index = _notes.indexOfFirst { it.id == note.id }
        if (index == -1) {
            _notes.add(note)
        } else {
            _notes[index] = note
        }
    }

    fun findOrCreate(id: Long): NotePreview = notes.find { note ->
        note.id == id
    } ?: NotePreview(
        id = notes.maxOf(NotePreview::id).inc(),
        name = "",
    )

    fun delete(id: Long) {
        _notes.removeIf { it.id == id }
    }

    fun addCollection(collection: CollectionPreview) {
        collection.notes.forEach { note ->
            val newNote = NotePreview(
                id = note.id,
                deadline = note.deadline,
                emoji = note.emoji,
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

    protected fun loadItems(): List<NotePreview> {
        val today = LocalDate.now()
        return listOf(
            NotePreview(
                id = 1,
                name = "My first notedddddddddddddddddddddddddddfffffffffffffffff",
                description = "My note descriptiondsdddddddddddddddddddddddddffffffffffffffffff",
            ),
            NotePreview(
                id = 2,
                emoji = Emoji("\uD83D\uDE3F"),
                name = "Забыть матан",
                done = false
            ),
            NotePreview(
                id = 3,
                emoji = Emoji("\uD83D\uDE13"),
                name = "Something",
                description = "Buy milk",
                done = false
            ),
            NotePreview(
                id = 4,
                deadline = Deadline.of(today),
                emoji = Emoji("\uD83D\uDE02"),
                name = "Не забыть про нюанс",
                collections = listOf("Приколы"),
                done = false
            ),
            NotePreview(
                id = 5,
                emoji = Emoji("\uD83D\uDE02"),
                name = "Там еще какой-то прикол был...",
                description = "Что-то про еврея, американца и русского",
                collections = listOf("Приколы")
            ),
            NotePreview(
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
            NotePreview(
                id = 7,
                name = "Отжаться 21 раз",
                done = true
            ),
            NotePreview(
                id = 8,
                name = "Отжаться 22 раз",
                done = true
            ),
            NotePreview(
                id = 9,
                name = "Отжаться 23 раз",
                done = true
            ),
            NotePreview(
                id = 10,
                name = "Отжаться 24 раз",
                done = true
            )
        )
    }
}