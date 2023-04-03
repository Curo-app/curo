package io.github.curo.ui.base

import android.view.View
import androidx.activity.compose.BackHandler
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.rememberDismissState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.curo.R
import io.github.curo.data.CollectionName
import io.github.curo.data.Deadline
import io.github.curo.data.Emoji
import io.github.curo.data.Note
import io.github.curo.data.FeedViewModel
import io.github.curo.data.TimedDeadline
import io.github.curo.ui.screens.SwipeBackground
import io.github.curo.utils.DateTimeUtils.dateFormatter
import io.github.curo.utils.DateTimeUtils.timeShortFormatter
import java.time.LocalDate


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun Feed(
    modifier: Modifier = Modifier,
    onNoteClick: (Note) -> Unit,
    onCollectionClick: (CollectionName) -> Unit,
    viewModel: FeedViewModel,
) {
    LazyColumn(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
            .wrapContentSize()
    ) {
        items(viewModel.items, { it.id }) { item ->
            val currentItem by rememberUpdatedState(item)
            val dismissState = rememberDismissState(
                confirmStateChange = {
                    if (it == DismissValue.DismissedToStart) {
                        viewModel.delete(currentItem.id)
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
                    )
                }
            )
        }
    }
}

@Composable
fun FeedForced(
    modifier: Modifier = Modifier,
    onNoteClick: (Note) -> Unit,
    content: List<Note>,
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
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    item: Note,
    onNoteClick: (Note) -> Unit,
    onCollectionClick: ((CollectionName) -> Unit)?,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    ListItem(
        modifier = modifier.cardModifier(interactionSource) { onNoteClick(item) },
        headlineText = { FeedItemHeader(item) },
        supportingText = onCollectionClick?.let { feedItemSupportingTextFactory(item, it) },
        leadingContent = { EmojiContainer(item.emoji) },
        overlineText = feedItemDeadlineFactory(item),
        trailingContent = feedItemCheckboxFactory(item),
        colors = listItemColors(item.done != true),
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun listItemColors(enabled: Boolean): ListItemColors =
    if (enabled) {
        ListItemDefaults.colors()
    } else {
        ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.inverseOnSurface,
            headlineColor = Color.DarkGray
        )
    }

fun feedItemCheckboxFactory(item: Note): @Composable (() -> Unit)? =
    item.done?.let {
        {
            Checkbox(
                modifier = Modifier.size(20.dp),
                checked = it,
                onCheckedChange = { item.done = it }
            )
        }
    }

fun feedItemDeadlineFactory(item: Note): @Composable (() -> Unit)? =
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
private fun FeedItemHeader(item: Note) {
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
    item: Note,
    onCollectionClick: (CollectionName) -> Unit,
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
    name: CollectionName,
    onClick: (CollectionName) -> Unit,
) {
    SuggestionChip(
        modifier = Modifier
            .padding(end = 4.dp)
            .height(30.dp),
        onClick = { onClick(name) },
        label = {
            Text(text = name.name)
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NoteCardPreview() {
    val viewModel = remember { FeedViewModel() }
    var fabButtonState: FABButtonState by remember { mutableStateOf(FABButtonState.Opened) }
    val transition =
        updateTransition(targetState = fabButtonState.opened(), label = "FABAddMenuTransition")
    val fabAnimationProperties = fabAnimationProperties(transition)

    Scaffold(
        content = { padding ->
            Feed(
                modifier = Modifier.padding(padding),
                viewModel = viewModel,
                onCollectionClick = {},
                onNoteClick = {},
            )

            FABAddMenu(
                fabButtonState = fabButtonState,
                onToggle = { fabButtonState = fabButtonState.act() },
                onClose = { fabButtonState = fabButtonState.act() },
                properties = fabAnimationProperties,
            )

            BackHandler(enabled = fabButtonState.opened(), onBack = {
                fabButtonState = fabButtonState.act()
            })
        }
    )
}