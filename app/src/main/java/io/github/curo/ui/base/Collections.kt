package io.github.curo.ui.base

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import io.github.curo.data.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Collections(viewModel: CollectionViewModel) {
    val itemIds by viewModel.itemIds.collectAsState()

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .background(color = MaterialTheme.colorScheme.background)
                .wrapContentSize()
        ) {
            itemsIndexed(viewModel.items.value) { index, item ->
                ExpandableCollectionView(
                    collection = item,
                    onNoteClick = {},
                    onCollectionClick = { viewModel.onItemClicked(index) },
                    isExpanded = itemIds.contains(index)
                )
            }
        }
    }
}

@Composable
fun ExpandableCollectionView(
    collection: CollectionPreviewModel,
    onNoteClick: (NotePreviewModel) -> Unit,
    onCollectionClick: (CollectionPreviewModel) -> Unit,
    isExpanded: Boolean
) {
    Box {
        Column {
            CollectionCard(collection, onCollectionClick)
            CollectionNotes(collection.notes, onNoteClick, isExpanded)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionCard(
    collection: CollectionPreviewModel,
    onCollectionClick: (CollectionPreviewModel) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    ListItem(
        modifier = Modifier.cardModifier(interactionSource) { onCollectionClick(collection) },
        headlineText = { CollectionsItemHeader(collection) },
        leadingContent = { EmojiContainer(collection.emoji) },
        trailingContent = collectionsProgressFactory(collection),
        colors = listItemColors(collection.progress?.run { total != done } ?: true)
    )
}

@Composable
fun CollectionNotes(
    notes: List<NotePreviewModel>,
    onNoteClick: (NotePreviewModel) -> Unit,
    isExpanded: Boolean
) {
    // Opening Animation
    val expandTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(300)
        ) + fadeIn(
            animationSpec = tween(300)
        )
    }

    // Closing Animation
    val collapseTransition = remember {
        shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(300)
        ) + fadeOut(
            animationSpec = tween(300)
        )
    }

    AnimatedVisibility(
        visible = isExpanded,
        enter = expandTransition,
        exit = collapseTransition
    ) {
        Box(
            modifier = Modifier
                .padding(start = 25.dp, top = 5.dp, end = 5.dp, bottom = 5.dp)
        ) {
            FeedForced(onNoteClick = onNoteClick, content = notes)
        }
    }
}



@Composable
private fun CollectionsItemHeader(item: CollectionPreviewModel) {
    Text(
        text = item.name,
        maxLines = 1,
        style = MaterialTheme.typography.titleMedium,
        overflow = TextOverflow.Ellipsis,
    )
}

private fun collectionsProgressFactory(item: CollectionPreviewModel): @Composable (() -> Unit)? =
    item.progress?.let {
        {
            Text(
                text = it.toString(),
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                fontSize = 4.em
            )
        }
    }

@Preview(showBackground = true)
@Composable
fun CollectionsScreenPreview() {
    val viewModel = remember { CollectionViewModel() }
    Collections(viewModel = viewModel)
}

@Preview(showBackground = true)
@Composable
fun ClosedCollectionPreview() {
    ExpandableCollectionView(
        collection = CollectionPreviewModel(
            emoji = Emoji("\uD83D\uDC7D"),
            name = "My super list",
            notes = listOf(
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
                )
            )
        ),
        onNoteClick = {},
        onCollectionClick = {},
        isExpanded = false
    )
}

@Preview(showBackground = true)
@Composable
fun OpenedCollectionPreview() {
    ExpandableCollectionView(
        collection = CollectionPreviewModel(
            emoji = Emoji("\uD83D\uDC7D"),
            name = "My super list",
            notes = listOf(
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
                )
            )
        ),
        onNoteClick = {},
        onCollectionClick = {},
        isExpanded = true
    )
}

@Preview(showBackground = true)
@Composable
fun FinishedCollectionPreview() {
    ExpandableCollectionView(
        collection = CollectionPreviewModel(
            emoji = Emoji("\uD83D\uDC7D"),
            name = "My super list",
            notes = listOf(
                NotePreviewModel(
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
                )
            )
        ),
        onNoteClick = {},
        onCollectionClick = {},
        isExpanded = true
    )
}
