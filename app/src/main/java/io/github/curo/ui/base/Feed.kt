package io.github.curo.ui.base

import android.view.View
import androidx.activity.compose.BackHandler
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import io.github.curo.data.NotePreviewModel
import io.github.curo.data.TimedDeadline
import io.github.curo.utils.DateTimeUtils.dateFormatter
import io.github.curo.utils.DateTimeUtils.timeShortFormatter
import java.util.Calendar
import java.util.Date


@Composable
fun Feed(
    modifier: Modifier = Modifier,
    onNoteClick: (NotePreviewModel) -> Unit,
    onCollectionClick: (CollectionName) -> Unit,
    content: List<NotePreviewModel>,
) {
    LazyColumn(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
            .wrapContentSize()
    ) {
        items(content) { item ->
            NoteCard(
                item = item,
                onNoteClick = onNoteClick,
                onCollectionClick = onCollectionClick,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteCard(
    item: NotePreviewModel,
    onNoteClick: (NotePreviewModel) -> Unit,
    onCollectionClick: (CollectionName) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    ListItem(
        modifier = Modifier.noteCardModifier(interactionSource) { onNoteClick(item) },
        headlineText = { FeedItemHeader(item) },
        supportingText = feedItemSupportingTextFactory(item, onCollectionClick),
        leadingContent = { EmojiContainer(item.emoji) },
        overlineText = feedItemDeadlineFactory(item),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
fun feedItemDeadlineFactory(item: NotePreviewModel): @Composable (() -> Unit)? =
    item.deadline?.let { deadline ->
        {
            val header = formatHeader(deadline)
            val hasWarn = hasWarn(deadline)
            Row {

                Text(
                    textAlign = TextAlign.Center,
                    text = header,
                    color = if (hasWarn) BadgeDefaults.containerColor
                    else MaterialTheme.colorScheme.outline,
                )
                if (hasWarn) {
                    Badge(Modifier.padding(start = 4.dp)) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "!",
                        )
                    }
                }
            }
        }
    }

@Composable
private fun FeedItemHeader(item: NotePreviewModel) {
    Text(
        text = item.name,
        maxLines = 1,
        style = MaterialTheme.typography.titleMedium,
        overflow = TextOverflow.Ellipsis,
    )
}

private fun Modifier.noteCardModifier(
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
    val afterTomorrow = Calendar.getInstance().apply {
        time = Date()
        set(Calendar.HOUR_OF_DAY, 0)
        add(Calendar.DATE, 2)
    }.time

    return deadline.date.before(afterTomorrow)
}

@Composable
private fun formatHeader(deadline: Deadline): String {
    val calendar = Calendar.getInstance().apply {
        time = Date()
        set(Calendar.HOUR_OF_DAY, 0)
    }

    val tomorrow = calendar.apply {
        add(Calendar.DATE, 1)
    }.time
    val afterTomorrow = calendar.apply {
        add(Calendar.DATE, 1)
    }.time


    val dateHeader = when {
        deadline.date.before(tomorrow) -> stringResource(R.string.today)
        deadline.date.before(afterTomorrow) -> stringResource(R.string.tomorrow)
        else -> dateFormatter.format(deadline.date)
    }

    val timeHeader = if (deadline is TimedDeadline) {
        ", ${timeShortFormatter.format(deadline.time)}"
    } else ""

    return dateHeader + timeHeader
}

private fun feedItemSupportingTextFactory(
    item: NotePreviewModel,
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
private fun EmojiContainer(item: Emoji) {
    AndroidView(
        factory = { context ->
            AppCompatTextView(context).apply {
                setTextColor(Color.Black.toArgb())
                text = item.value
                textSize = 30F
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NoteCardPreview() {
    val calendar = Calendar.getInstance().apply {
        time = Date()
    }

    fun Calendar.inc(): Calendar {
        add(Calendar.DATE, 1)
        return this
    }

    Scaffold(
        content = { padding ->
            Feed(
                modifier = Modifier.padding(padding),
                content = listOf(
                    NotePreviewModel(
                        name = "My first notedddddddddddddddddddddddddddfffffffffffffffff",
                        description = "My note descriptiondsdddddddddddddddddddddddddffffffffffffffffff",
                    ),
                    NotePreviewModel(
                        emoji = Emoji("\uD83D\uDE3F"),
                        name = "Забыть матан",
                    ),
                    NotePreviewModel(
                        emoji = Emoji("\uD83D\uDE13"),
                        name = "Something",
                        description = "Buy milk",
                    ),
                    NotePreviewModel(
                        deadline = Deadline.of(calendar.time),
                        emoji = Emoji("\uD83D\uDE02"),
                        name = "Не забыть про нюанс",
                        collections = listOf("Приколы").map { CollectionName(it) }
                    ),
                    NotePreviewModel(
                        deadline = Deadline.of(calendar.inc().time, Calendar.getInstance().time),
                        emoji = Emoji("\uD83D\uDE02"),
                        name = "Там еще какой-то прикол был...",
                        description = "Что-то про еврея, американца и русского",
                        collections = listOf("Приколы").map { CollectionName(it) }
                    ),
                    NotePreviewModel(
                        deadline = Deadline.of(calendar.inc().time),
                        emoji = Emoji("\uD83D\uDC7D"),
                        name = "FP HW 3",
                        description = "Надо быстрее сделать",
                        collections = listOf(
                            "Домашка",
                            "Важное",
                            "Haskell",
                            "Ненавижу ФП"
                        ).map { CollectionName(it) }
                    ),
                    NotePreviewModel(
                        name = "Отжаться 21 раз",
                    ),
                    NotePreviewModel(
                        name = "Отжаться 22 раза",
                    ),
                    NotePreviewModel(
                        name = "Отжаться 24 раза",
                    ),
                    NotePreviewModel(
                        name = "Отжаться 25 раз",
                    ),
                    NotePreviewModel(
                        name = "Отжаться 26 раз",
                    ),
                ),
                onCollectionClick = {},
                onNoteClick = {},
            )

            var isOpen by remember {
                mutableStateOf(false)
            }

            FABAddMenu(
                isOpen = isOpen,
                onToggle = { isOpen = !isOpen },
                onClose = { state, _ ->
                    if (state) {
                        isOpen = !isOpen
                    }
                })

            BackHandler(enabled = isOpen, onBack = {
                isOpen = !isOpen
            })
        }
    )
}