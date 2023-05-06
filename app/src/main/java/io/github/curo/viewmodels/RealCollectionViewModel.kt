package io.github.curo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Transaction
import io.github.curo.data.CollectionPreview
import io.github.curo.database.dao.CollectionDao
import io.github.curo.database.dao.NoteCollectionCrossRefDao
import io.github.curo.database.dao.NoteDao
import io.github.curo.database.entities.*
import io.github.curo.database.entities.Collection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RealCollectionViewModel(
    private val noteDao: NoteDao,
    private val collectionDao: CollectionDao,
    private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
) : ViewModel() {
    fun getAll(): Flow<List<CollectionPreview>> =
        collectionDao.getAll()
            .map { l -> l.map { CollectionPreview.of(it) } }

    fun find(collectionName: String): Flow<CollectionPreview?> =
        collectionDao.find(collectionName)
            .map { collection -> collection?.let { CollectionPreview.of(it) } }


    class RealCollectionViewModelFactory(
        private val noteDao: NoteDao,
        private val collectionDao: CollectionDao,
        private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RealCollectionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RealCollectionViewModel(
                    noteDao,
                    collectionDao,
                    noteCollectionCrossRefDao
                ) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}