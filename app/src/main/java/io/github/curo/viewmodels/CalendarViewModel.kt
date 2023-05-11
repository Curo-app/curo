package io.github.curo.viewmodels

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.curo.data.NotePreview
import io.github.curo.data.NotePreview.Companion.extractCollections
import io.github.curo.database.dao.CollectionDao
import io.github.curo.database.dao.NoteCollectionCrossRefDao
import io.github.curo.database.entities.CollectionInfo
import io.github.curo.database.dao.NoteDao
import io.github.curo.utils.setAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

data class CalendarCollectionsState(
    val collectionNames: Map<CollectionInfo, CalendarViewModel.CollectionFilter>
) {
    fun getCollectionFilters(): List<CalendarViewModel.CollectionFilter> =
        collectionNames.values.toList()
}

@Stable
class CalendarViewModel(
    private val noteDao: NoteDao,
    collectionDao: CollectionDao,
    private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
) : FeedViewModel(noteDao) {

    val collectionsState: StateFlow<CalendarCollectionsState> =
        collectionDao.getAll()
            .map { collections -> collections.map { CollectionInfo.of(it) } }
            .map { names -> CalendarCollectionsState(names.associateWith { CollectionFilter(it) }) }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = CalendarCollectionsState(emptyMap())
            )

    private var _currentDay by mutableStateOf(LocalDate.now())
    val currentDay: LocalDate = _currentDay

    fun setDay(day: LocalDate) {
        _currentDay = day
        _notes.setAll(super.notes.filter { note ->
            note.deadline?.let { it.date == day } ?: false
        })
    }

    private val _collectionsNames = mutableStateMapOf<CollectionInfo, CollectionFilter>()
//    val collectionsNames: List<CollectionFilter> get() = _collectionsNames.values.toList()

    private val _notes = mutableStateListOf<NotePreview>()

    // field is never used, but should be in DayNotes.kt
    override val notes: List<NotePreview>
        get() = _notes

    private val _dayState = mutableStateMapOf<LocalDate, DayState>()
    val dayState: Map<LocalDate, DayState>
        get() = _dayState

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val items = loadItems()
                val collections = items
                    .extractCollections()
                    .associateWith { CollectionFilter(it) }

                val dateCounted = getDateGrouped(items)

                withContext(Dispatchers.Main) {
                    _collectionsNames.putAll(collections)
                    setDateGrouped(dateCounted)
                }
            }
        }
    }

    private fun resetFilters() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val items = noteDao.getAll().first().map { NotePreview.of(it) }

                val dateCounted = getDateGrouped(items)

                withContext(Dispatchers.Main) {
                    setDateGrouped(dateCounted)
                }
            }
        }
    }

    // onCollectionFilterClick
    // set day State for each day
    fun updateOnFilters() {
        viewModelScope.launch {
            val showedCollections = buildSet {
                collectionsState.value.collectionNames.forEach { (name, filter) ->
                    if (filter.enabled) {
                        add(name)
                    }
                }
            }
            if (showedCollections.isEmpty()) {
                resetFilters()
                return@launch
            }

            val filteredNotesIds = showedCollections
                .map { noteCollectionCrossRefDao.listByCollectionId(it.collectionId).first() }
                .flatten()
                .map { it.noteId }
                .distinct()

            val filteredNotes =
                noteDao.findAll(filteredNotesIds).first()
                    .map { NotePreview.of(it) }

            val dateCounted = getDateGrouped(filteredNotes)

            withContext(Dispatchers.Main) {
                setDateGrouped(dateCounted)
            }
        }
    }

    private fun setDateGrouped(dateCounted: Map<LocalDate?, DayState>) {
        _dayState.clear()
        dateCounted
            .forEach { (t, u) ->
                t?.let {
                    _dayState[it] = u
                }
            }
    }

    private fun getDateGrouped(items: List<NotePreview>): Map<LocalDate?, DayState> {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        fun hasWarn(v: List<NotePreview>): Boolean = v.any {
            (it.deadline?.date == today || it.deadline?.date == tomorrow) &&
                    it.done == false
        }
        return items
            .groupBy { it.deadline?.date }
            .mapValues { (_, v) ->
                val count = v.count { it.deadline != null }

                when {
                    count == 0 -> DayState.Empty
                    hasWarn(v) -> DayState.Warn(count)
                    else -> DayState.NoWarn(count)
                }
            }
    }

    @Stable
    data class CollectionFilter(
        val name: CollectionInfo,
    ) {
        var enabled: Boolean by mutableStateOf(false)
    }

    @Immutable
    sealed interface DayState {
        @Immutable
        @JvmInline
        value class Warn(val amount: Int) : DayState

        @Immutable
        @JvmInline
        value class NoWarn(val amount: Int) : DayState

        object Empty : DayState
    }

    class CalendarViewModelFactory(
        private val noteDao: NoteDao,
        private val collectionDao: CollectionDao,
        private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CalendarViewModel(noteDao, collectionDao, noteCollectionCrossRefDao) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}