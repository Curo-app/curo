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
import io.github.curo.database.dao.NoteDao
import io.github.curo.utils.setAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Stable
class CalendarViewModel(
    noteDao: NoteDao
) : FeedViewModel(noteDao) {
    private var _currentDay by mutableStateOf(LocalDate.now())
    val currentDay: LocalDate = _currentDay

    fun setDay(day: LocalDate) {
        _currentDay = day
        _notes.setAll(super.notes.filter { note ->
            note.deadline?.let { it.date == day } ?: false
        })
    }

    private val _collectionsNames = mutableStateMapOf<String, CollectionFilter>()
    val collectionsNames: List<CollectionFilter> get() = _collectionsNames.values.toList()

    private val _notes = mutableStateListOf<NotePreview>()
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
                val items = super.notes

                val dateCounted = getDateGrouped(items)

                withContext(Dispatchers.Main) {
                    setDateGrouped(dateCounted)
                }
            }
        }
    }

    fun updateOnFilters() {
        viewModelScope.launch {
            val showedCollections = buildSet {
                _collectionsNames.forEach { (name, filter) ->
                    if (filter.enabled) {
                        add(name)
                    }
                }
            }
            if (showedCollections.isEmpty()) {
                resetFilters()
                return@launch
            }

            val filteredNotes = super.notes.filter { note ->
                note.collections.any { it in showedCollections }
            }

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
        val name: String,
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
        private val noteDao: NoteDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CalendarViewModel(noteDao) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}