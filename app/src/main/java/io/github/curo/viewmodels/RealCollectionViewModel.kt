package io.github.curo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.curo.data.CollectionPreview
import io.github.curo.database.dao.CollectionDao
import io.github.curo.database.dao.NoteCollectionCrossRefDao
import io.github.curo.database.dao.NoteDao
import io.github.curo.database.entities.*
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

    fun find(collectionId: Long): Flow<CollectionPreview?> =
        collectionDao.find(collectionId)
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