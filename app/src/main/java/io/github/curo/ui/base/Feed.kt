package io.github.curo.ui.base

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.rememberDismissState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.curo.R
import io.github.curo.data.Deadline
import io.github.curo.data.Emoji
import io.github.curo.data.NotePreview
import io.github.curo.data.SwipeDeleteProperties
import io.github.curo.data.TimedDeadline
import io.github.curo.database.entities.CollectionInfo
import io.github.curo.utils.DateTimeUtils.dateFormatter
import io.github.curo.utils.DateTimeUtils.timeShortFormatter
import io.github.curo.viewmodels.FeedViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun Feed(
    modifier: Modifier = Modifier,
    onNoteClick: (NotePreview) -> Unit,
    onCollectionClick: (CollectionInfo) -> Unit,
    onChecked: (NotePreview) -> Unit,
    viewModel: FeedViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val feedUiState by viewModel.feedUiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
            .wrapContentSize()
    ) {
        items(feedUiState.notes, { it.id }) { item ->
            val currentItem by rememberUpdatedState(item)
            val dismissState = rememberDismissState(
                confirmStateChange = {
                    if (it == DismissValue.DismissedToStart) {
                        coroutineScope.launch {
                            viewModel.deleteNote(currentItem.id)
                        }
                        true
                    } else false
                }
            )
            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier
                    .padding(vertical = 1.dp)
                    .animateItemPlacement(),
                directions = setOf(DismissDirection.EndToStart),
                dismissThresholds = { FractionalThreshold(0.33f) },
                background = {
                    SwipeBackground(dismissState)
                },
                dismissContent = {
                    NoteCard(
                        item = item,
                        onNoteClick = onNoteClick,
                        onCollectionClick = onCollectionClick,
                        onChecked = onChecked,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun SwipeBackground(dismissState: DismissState) {
    val color by animateColorAsState(
        if (dismissState.targetValue == DismissValue.DismissedToStart) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.surface
        }
    )

    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = SwipeDeleteProperties.alignment
    ) {
        Icon(
            imageVector = SwipeDeleteProperties.icon,
            contentDescription = stringResource(SwipeDeleteProperties.contentDescriptionId),
            modifier = Modifier.scale(scale),
            tint = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
fun FeedForced(
    modifier: Modifier = Modifier,
    onNoteClick: (NotePreview) -> Unit,
    onChecked: (NotePreview) -> Unit,
    content: List<NotePreview>,
) {
    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
            .wrapContentSize()
    ) {
        content.forEach { item ->
            NoteCard(
                item = item,
                onNoteClick = onNoteClick,
                onCollectionClick = null,
                onChecked = onChecked,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
    }
}

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    item: NotePreview,
    onNoteClick: (NotePreview) -> Unit,
    onCollectionClick: ((CollectionInfo) -> Unit)?,
    onChecked: (NotePreview) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    ListItem(
        modifier = modifier.cardModifier(interactionSource) { onNoteClick(item) },
        headlineContent = { FeedItemHeader(item) },
        supportingContent = onCollectionClick?.let { feedItemSupportingTextFactory(item, it) },
        leadingContent = { EmojiContainer(item.emoji) },
        overlineContent = feedItemDeadlineFactory(item),
        trailingContent = feedItemCheckboxFactory(item, onChecked),
        colors = listItemColors(item.done != true),
    )
}

@Composable
fun listItemColors(enabled: Boolean): ListItemColors =
    if (enabled) {
        ListItemDefaults.colors()
    } else {
        ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.inverseOnSurface,
            headlineColor = Color.DarkGray
        )
    }

fun feedItemCheckboxFactory(
    item: NotePreview,
    onChecked: (NotePreview) -> Unit,
): @Composable (() -> Unit)? =
    item.done?.let {
        {
            FilledIconToggleButton(
                modifier = Modifier.size(25.dp),
                onCheckedChange = { onChecked(item) },
                shape = MaterialTheme.shapes.small,
                checked = it,
            ) {
                if (it) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "aboba",
                    )
                }
            }
        }
    }

fun feedItemDeadlineFactory(item: NotePreview): @Composable (() -> Unit)? =
    item.deadline?.let { deadline ->
        {
            val header = formatHeader(deadline)
            val hasWarn = hasWarn(deadline)
            val color = when {
                hasWarn && item.done == true -> MaterialTheme.colorScheme.secondary
                hasWarn -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.outline
            }

            Row {
                Text(
                    textAlign = TextAlign.Center,
                    text = header,
                    color = color,
                )
                if (hasWarn) {
                    Icon(
                        imageVector = if (item.done == true) Icons.Filled.CheckCircle
                        else Icons.Filled.Error,
                        contentDescription = stringResource(R.string.done),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(15.dp),
                        tint = color,
                    )
                }
            }
        }
    }

@Composable
private fun FeedItemHeader(item: NotePreview) {
    Text(
        text = item.name,
        maxLines = 1,
        style = MaterialTheme.typography.titleMedium,
        overflow = TextOverflow.Ellipsis,
    )
}

fun Modifier.cardModifier(
    interactionSource: MutableInteractionSource,
    onNoteClick: () -> Unit,
) = composed {
    padding(4.dp)
        .clip(shape = MaterialTheme.shapes.medium)
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium
        )
        .background(MaterialTheme.colorScheme.primaryContainer)
        .clickable(
            interactionSource = interactionSource,
            indication = rememberRipple(),
            enabled = true,
            role = Role.Button,
            onClick = onNoteClick
        )
}

private fun hasWarn(deadline: Deadline): Boolean {
    val afterTomorrow = LocalDate.now().plusDays(2)
    return deadline.date < afterTomorrow
}

@Composable
private fun formatHeader(deadline: Deadline): String {
    val today = remember { LocalDate.now() }
    val tomorrow = remember { today.plusDays(1) }
    val afterTomorrow = remember { tomorrow.plusDays(1) }

    val dateHeader = when {
        deadline.date < tomorrow -> stringResource(R.string.today)
        deadline.date < afterTomorrow -> stringResource(R.string.tomorrow)
        else -> dateFormatter.format(deadline.date)
    }

    val timeHeader = if (deadline is TimedDeadline) {
        ", ${timeShortFormatter.format(deadline.time)}"
    } else ""

    return dateHeader + timeHeader
}

private fun feedItemSupportingTextFactory(
    item: NotePreview,
    onCollectionClick: (CollectionInfo) -> Unit,
): @Composable (() -> Unit)? = if (item.description != null || item.collections.isNotEmpty()) {
    {
        Column {
            item.description?.let {
                Text(
                    text = it,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            LazyRow(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.Top
            ) {
                items(item.collections) {
                    CollectionChip(it, onCollectionClick)
                }
            }
        }
    }
} else null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionChip(
    name: CollectionInfo,
    onClick: (CollectionInfo) -> Unit,
) {
    SuggestionChip(
        modifier = Modifier
            .padding(end = 4.dp)
            .height(30.dp),
        onClick = { onClick(name) },
        label = {
            Text(text = name.collectionName)
        })
}

@Composable
fun EmojiContainer(item: Emoji, size: Float = 30F) {
    AndroidView(
        factory = { context ->
            AppCompatTextView(context).apply {
                setTextColor(Color.Black.toArgb())
                text = item.value
                textSize = size
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
        },
    )
}

//@Preview
//@Composable
//fun NoteCardPreview() {
//    val viewModel = remember { FeedViewModel() }
//    Feed(
//        viewModel = viewModel,
//        onCollectionClick = {},
//        onNoteClick = {},
//    )
//}