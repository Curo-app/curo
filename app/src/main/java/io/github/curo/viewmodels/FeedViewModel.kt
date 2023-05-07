package io.github.curo.viewmodels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.curo.data.Deadline
import io.github.curo.data.Emoji
import io.github.curo.data.NotePreview
import io.github.curo.database.entities.CollectionInfo
import io.github.curo.database.dao.NoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

data class FeedUiState(
    val notes: List<NotePreview> = emptyList(),
)

@Stable
open class FeedViewModel(
    private val noteDao: NoteDao
) : ViewModel() {
    private val _notes = mutableStateListOf<NotePreview>()
    open val notes: List<NotePreview>
        get() = _notes

    val feedUiState: StateFlow<FeedUiState> =
        getAll().map { FeedUiState(it) }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = FeedUiState()
            )

    private fun getAll(): Flow<List<NotePreview>> =
        noteDao.getAll()
            .map { l -> l.map { NotePreview.of(it) } }

//    fun update(note: NotePreview) {
//        val index = _notes.indexOfFirst { it.id == note.id }
//        if (index == -1) {
//            _notes.add(note)
//        } else {
//            _notes[index] = note
//        }
//    }

//    fun findOrCreate(id: Long): NotePreview = notes.find { note ->
//        note.id == id
//    } ?: NotePreview(
//        id = notes.maxOf(NotePreview::id).inc(),
//        name = "",
//    )

//    fun deleteNote(id: Long) {
//        _notes.removeIf { it.id == id }
//    }

    suspend fun deleteNote(noteId: Long) = noteDao.delete(noteId)

//    fun addCollection(collection: CollectionPreview) {
//        collection.notes.forEach { note ->
//            val newNote = NotePreview(
//                id = note.id,
//                deadline = note.deadline,
//                emoji = note.emoji,
//                name = note.name,
//                description = note.description,
//                collections = note.collections + collection.name,
//                done = note.done,
//            )
//            update(newNote)
//        }
//    }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                _notes.addAll(getAll().first())
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
                collections = listOf(CollectionInfo(0, "Нюансы")),
                done = false
            ),
            NotePreview(
                id = 5,
                emoji = Emoji("\uD83D\uDE02"),
                name = "Там еще какой-то прикол был...",
                description = "Что-то про еврея, американца и русского",
                collections = listOf(CollectionInfo(1, "Приколы")),
                done = false
            ),
            NotePreview(
                id = 6,
                deadline = Deadline.of(today.plusDays(1)),
                emoji = Emoji("\uD83D\uDC7D"),
                name = "FP HW 3",
                description = "Надо быстрее сделать",
                collections = listOf(
                    CollectionInfo(1, "Домашка"),
                    CollectionInfo(1, "Важное"),
                    CollectionInfo(1, "Haskell"),
                    CollectionInfo(1, "Ненавижу ФП")
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

    open class FeedViewModelFactory(
        private val noteDao: NoteDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FeedViewModel(noteDao) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }

    }
}