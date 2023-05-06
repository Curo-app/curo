package io.github.curo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import io.github.curo.R
import io.github.curo.data.*
import io.github.curo.ui.base.EmojiContainer
import io.github.curo.ui.base.FeedForced
import io.github.curo.ui.base.cardModifier
import io.github.curo.ui.base.listItemColors
import kotlin.random.Random
import androidx.compose.foundation.lazy.items
import io.github.curo.database.entities.CollectionInfo
import io.github.curo.viewmodels.CollectionViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Collections(
    onCollectionClick: (String) -> Unit,
    onNoteClick: (NotePreview) -> Unit,
    viewModel: CollectionViewModel,
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .background(color = MaterialTheme.colorScheme.background)
                .wrapContentSize()
        ) {
            items(viewModel.collections) { collection ->
                ExpandableCollectionView(
                    collection = collection,
                    onNoteClick = onNoteClick,
                    onCollectionClick = onCollectionClick,
                    isExpanded = collection.name in viewModel.expanded,
                    onCollectionExpand = { viewModel.expand(collection.name) },
                )
            }
        }
    }
}

@Composable
fun ExpandableCollectionView(
    collection: CollectionPreview,
    onNoteClick: (NotePreview) -> Unit,
    onCollectionClick: (String) -> Unit,
    onCollectionExpand: () -> Unit,
    isExpanded: Boolean,
) {
    Box {
        Column {
            CollectionCard(collection, onCollectionClick, onCollectionExpand, isExpanded)
            CollectionNotes(collection.notes, onNoteClick, isExpanded)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionCard(
    collection: CollectionPreview,
    onCollectionClick: (String) -> Unit,
    onCollectionExpand: () -> Unit,
    isExpanded: Boolean,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    ListItem(
        modifier = Modifier.cardModifier(interactionSource) {
            onCollectionClick(CollectionInfo(collection.id, collection.name))
        },
        headlineText = { CollectionsItemHeader(collection) },
        leadingContent = { EmojiContainer(collection.emoji) },
        trailingContent = { CollectionTrailing(collection, isExpanded, onCollectionExpand) },
        colors = listItemColors(collection.progress?.run { !isFinished() } ?: true)
    )
}

@Composable
fun CollectionNotes(
    notes: List<NotePreview>,
    onNoteClick: (NotePreview) -> Unit,
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
private fun CollectionsItemHeader(item: CollectionPreview) {
    Text(
        text = item.name,
        maxLines = 1,
        style = MaterialTheme.typography.titleMedium,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun CollectionTrailing(
    item: CollectionPreview,
    isExpanded: Boolean,
    onExpand: () -> Unit,
) {
    val arrowRotation by updateTransition(
        targetState = isExpanded,
        label = "ExpandCollectionArrowTransition"
    ).animateFloat(
        label = "ExpandCollectionArrowRotation",
        transitionSpec = { tween(delayMillis = 100) }
    ) {
        if (it) 90f
        else 0f
    }

    Row(
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onExpand() },
        )
    ) {
        item.progress?.let {
            Text(
                text = it.toString(),
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                fontSize = 4.em
            )
        }
        Icon(
            modifier = Modifier.rotate(arrowRotation),
            imageVector = Icons.Rounded.ArrowDropDown,
            contentDescription = stringResource(R.string.expand_collection_notes),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CollectionsScreenPreview() {
    val viewModel = remember { CollectionViewModel() }
    Collections(viewModel = viewModel, onCollectionClick = {}, onNoteClick = {})
}

@Preview(showBackground = true)
@Composable
fun ClosedCollectionPreview() {
    ExpandableCollectionView(
        collection = CollectionPreview(
            emoji = Emoji("\uD83D\uDC7D"),
            name = "My super list",
            notes = listOf(
                NotePreview(
                    id = Random.nextLong(),
                    name = "My first notedddddddddddddddddddddddddddfffffffffffffffff",
                    description = "My note descriptiondsdddddddddddddddddddddddddffffffffffffffffff",
                ),
                NotePreview(
                    id = Random.nextLong(),
                    emoji = Emoji("\uD83D\uDE3F"),
                    name = "Забыть матан",
                    done = false
                ),
                NotePreview(
                    id = Random.nextLong(),
                    emoji = Emoji("\uD83D\uDC7D"),
                    name = "FP HW 3",
                    description = "Надо быстрее сделать",
                    collections = listOf(
                        "Домашка",
                        "Важное",
                        "Haskell",
                        "Ненавижу ФП"
                    ),
                    done = true
                )
            )
        ),
        onNoteClick = {},
        onCollectionClick = {},
        isExpanded = false,
        onCollectionExpand = {},
    )
}

@Preview(showBackground = true)
@Composable
fun OpenedCollectionPreview() {
    ExpandableCollectionView(
        collection = CollectionPreview(
            emoji = Emoji("\uD83D\uDC7D"),
            name = "My super list",
            notes = listOf(
                NotePreview(
                    id = Random.nextLong(),
                    name = "My first notedddddddddddddddddddddddddddfffffffffffffffff",
                    description = "My note descriptiondsdddddddddddddddddddddddddffffffffffffffffff",
                ),
                NotePreview(
                    id = Random.nextLong(),
                    emoji = Emoji("\uD83D\uDE3F"),
                    name = "Забыть матан",
                    done = false
                ),
                NotePreview(
                    id = Random.nextLong(),
                    emoji = Emoji("\uD83D\uDC7D"),
                    name = "FP HW 3",
                    description = "Надо быстрее сделать",
                    collections = listOf(
                        "Домашка",
                        "Важное",
                        "Haskell",
                        "Ненавижу ФП"
                    ),
                    done = true
                )
            )
        ),
        onNoteClick = {},
        onCollectionClick = {},
        isExpanded = true,
        onCollectionExpand = {},
    )
}

@Preview(showBackground = true)
@Composable
fun FinishedCollectionPreview() {
    ExpandableCollectionView(
        collection = CollectionPreview(
            emoji = Emoji("\uD83D\uDC7D"),
            name = "My super list",
            notes = listOf(
                NotePreview(
                    id = Random.nextLong(),
                    emoji = Emoji("\uD83D\uDC7D"),
                    name = "FP HW 3",
                    description = "Надо быстрее сделать",
                    collections = listOf(
                        "Домашка",
                        "Важное",
                        "Haskell",
                        "Ненавижу ФП"
                    ),
                    done = true
                )
            )
        ),
        onNoteClick = {},
        onCollectionClick = {},
        isExpanded = true,
        onCollectionExpand = {},
    )
}
