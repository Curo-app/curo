package io.github.curo.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import io.github.curo.R
import io.github.curo.viewmodels.CollectionViewModel
import io.github.curo.data.Deadline
import io.github.curo.viewmodels.NotePatchViewModel
import io.github.curo.data.SimpleDeadline
import io.github.curo.data.TimedDeadline
import io.github.curo.utils.DateTimeUtils.dateFormatter
import io.github.curo.utils.DateTimeUtils.timeShortFormatter
import io.github.curo.utils.MAX_NOTE_COLLECTIONS_AMOUNT
import java.time.LocalDate
import java.time.LocalTime

private val emptyTextFieldValue = TextFieldValue("", TextRange.Zero)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteOptionsScreen(
    note: NotePatchViewModel,
    collectionViewModel: CollectionViewModel,
    onReturn: () -> Unit,
) {
    Scaffold(
        topBar = { NoteOptionsTopBar(onReturn) },
        content = {
            NoteOptions(
                modifier = Modifier.padding(it),
                note = note,
                collectionViewModel = collectionViewModel,
            )
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NoteOptionsTopBar(onReturn: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = "Settings", style = MaterialTheme.typography.titleLarge)
        },
        navigationIcon = {
            IconButton(onClick = onReturn) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Discard changes")
            }
        }
    )
}

@Composable
private fun SettingsDivider() = Divider(modifier = Modifier.padding(vertical = 10.dp))

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun NoteOptions(
    modifier: Modifier = Modifier,
    note: NotePatchViewModel,
    collectionViewModel: CollectionViewModel,
) {
    val today = remember { LocalDate.now() }

    var dateSelectDialogOpened by remember { mutableStateOf(false) }
    var timeSelectDialogOpened by remember { mutableStateOf(false) }

    if (dateSelectDialogOpened) {
        DatePickerDialog(
            initialDate = today,
            onDismissRequest = { dateSelectDialogOpened = false },
            onDateChange = {
                note.deadline = changeDate(note.deadline, it)
                dateSelectDialogOpened = false
            }
        )
    }

    if (timeSelectDialogOpened) {
        TimePickerDialog(
            onDismissRequest = { timeSelectDialogOpened = false },
            onTimeChange = {
                note.deadline = changeTime(note.deadline, it)
                timeSelectDialogOpened = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start,
    ) {
        NoteCheckbox(
            checked = note.hasCheckbox,
            onCheckedChange = { note.hasCheckbox = it },
        )

        SettingsDivider()

        DeadlineSelector(
            deadline = note.deadline,
            onDateClick = { dateSelectDialogOpened = true },
            onTimeClick = { timeSelectDialogOpened = true },
            onDateClear = { note.deadline = null },
            onTimeClear = { note.deadline = SimpleDeadline(it.date) },
        )

        SettingsDivider()

        CollectionAdder(collectionViewModel, note.collections)
        CurrentCollections(note.newCollection, note.collections)
    }
}

@Composable
private fun DeadlineSelector(
    deadline: Deadline?,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onDateClear: (Deadline) -> Unit,
    onTimeClear: (TimedDeadline) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        DateDeadlineChip(
            deadline = deadline,
            onClick = onDateClick,
            onClear = onDateClear,
        )

        Spacer(modifier = Modifier.width(10.dp))

        TimeDeadlineChip(
            deadline = deadline,
            onClick = onTimeClick,
            onClear = onTimeClear
        )
    }
}

@Composable
private fun DateDeadlineChip(
    deadline: Deadline?,
    onClick: () -> Unit,
    onClear: (Deadline) -> Unit,
) {
    when (deadline) {
        is SimpleDeadline, is TimedDeadline -> ExistingChip(
            label = deadline.date.format(dateFormatter),
            icon = Icons.Rounded.Today,
            onClick = onClick,
            onClear = { onClear(deadline) }
        )

        null -> EmptyChip(
            label = stringResource(R.string.setup_deadline_date),
            icon = Icons.Rounded.Today,
            onClick = onClick,
        )
    }
}

@Composable
private fun TimeDeadlineChip(
    deadline: Deadline?,
    onClick: () -> Unit,
    onClear: (TimedDeadline) -> Unit,
) {
    when (deadline) {
        is TimedDeadline -> ExistingChip(
            label = deadline.time.format(timeShortFormatter),
            icon = Icons.Rounded.AccessTime,
            onClick = onClick,
            onClear = { onClear(deadline) },
        )

        is SimpleDeadline, null -> EmptyChip(
            label = stringResource(R.string.setup_deadline_time),
            icon = Icons.Rounded.AccessTime,
            onClick = onClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExistingChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    onClear: () -> Unit,
) {
    InputChip(
        selected = true,
        onClick = onClick,
        label = {
            Text(text = label)
        },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = label)
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Rounded.Clear,
                contentDescription = stringResource(R.string.clear),
                modifier = Modifier.clickable(onClick = onClear),
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptyChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    InputChip(
        selected = true,
        onClick = onClick,
        label = { Text(text = label) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = label) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionAdder(
    collectionViewModel: CollectionViewModel,
    collections: SnapshotStateList<String>,
) {
    var suggestionState: Suggestion by remember { mutableStateOf(Suggestion.Hidden) }
    var textFieldValue by remember { mutableStateOf(emptyTextFieldValue) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val expanded = suggestionState == Suggestion.Shown
                && collectionViewModel.query.isNotEmpty()
                && textFieldValue.text.isNotBlank()

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { suggestionState = Suggestion.Hidden }
        ) {
            CollectionNameTextField(
                modifier = Modifier.menuAnchor(),
                isError = collections.size == MAX_NOTE_COLLECTIONS_AMOUNT,
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    if (textFieldValue.text.isNotBlank()) {
                        collectionViewModel.query = textFieldValue.text
                    }
                    suggestionState = when (suggestionState) {
                        Suggestion.Hidden -> Suggestion.Shown
                        Suggestion.Shown -> Suggestion.Shown
                        Suggestion.Suggested -> Suggestion.Hidden
                    }
                },
                onClear = {
                    suggestionState = Suggestion.Suggested
                    textFieldValue = emptyTextFieldValue
                },
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { suggestionState = Suggestion.Hidden },
                modifier = Modifier.width(300.dp),
            ) {
                collectionViewModel.suggestions.forEach { label ->
                    DropdownMenuItem(
                        onClick = {
                            suggestionState = Suggestion.Suggested
                            textFieldValue = TextFieldValue(
                                text = label,
                                selection = TextRange(label.length)
                            )
                        },
                        text = { Text(text = label) }
                    )
                }
            }
        }

        AddCollectionButton(
            enabled = textFieldValue.text.isNotBlank() &&
                    collections.size != MAX_NOTE_COLLECTIONS_AMOUNT,
            onClick = {
                suggestionState = Suggestion.Suggested
                collections += textFieldValue.text
                textFieldValue = TextFieldValue("", selection = TextRange.Zero)
            },
        )
    }
}

@Composable
private fun AddCollectionButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    FilledIconButton(
        modifier = Modifier.padding(vertical = 10.dp),
        enabled = enabled,
        onClick = {
            focusManager.clearFocus()
            onClick()
        },
        colors = IconButtonDefaults.filledIconButtonColors()
    ) { Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.add)) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionNameTextField(
    modifier: Modifier,
    isError: Boolean,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onClear: (TextFieldValue) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        isError = isError,
        modifier = modifier
            .width(300.dp)
            .focusRequester(focusRequester),
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.collection_name)) },
        singleLine = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Rounded.Clear,
                contentDescription = stringResource(R.string.clear_input),
                modifier = Modifier.clickable { onClear(value) },
            )
        },
        supportingText = {
            if (isError) {
                Text(text = stringResource(R.string.maximum_collections_amount_reached))
            }
        }
    )
}

@Composable
private fun NoteCheckbox(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?) {
    val icon: (@Composable () -> Unit)? = toggleIconFactory(checked)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.with_checkbox),
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            thumbContent = icon
        )
    }
}

private fun changeTime(
    deadline: Deadline?,
    it: LocalTime
) = when (deadline) {
    is SimpleDeadline -> Deadline.of(deadline.date, it)
    is TimedDeadline -> deadline.copy(time = it)
    null -> error("Time cannot be selected without date")
}

private fun changeDate(
    deadline: Deadline?,
    it: LocalDate
) = when (deadline) {
    is SimpleDeadline, null -> Deadline.of(it)
    is TimedDeadline -> deadline.copy(date = it)
}

private fun toggleIconFactory(mIsTodoNote: Boolean): @Composable (() -> Unit)? = if (mIsTodoNote) {
    {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            modifier = Modifier.size(SwitchDefaults.IconSize),
        )
    }
} else null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrentCollections(
    patchCollection: String?,
    collections: MutableList<String>
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        patchCollection?.let { collection ->
            item {
                SuggestionChip(onClick = { /* DO NOTHING */ },
                    modifier = Modifier.padding(vertical = 0.dp),
                    interactionSource = remember { MutableInteractionSource() },
                    label = { Text(text = collection) },
                )
            }
        }
        items(collections) { collection ->
            CollectionChip(collection, collections)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CollectionChip(
    current: String,
    collections: MutableList<String>
) {
    SuggestionChip(onClick = { /* DO NOTHING */ },
        modifier = Modifier.padding(vertical = 0.dp),
        interactionSource = remember { MutableInteractionSource() },
        label = { Text(text = current) },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Clear,
                contentDescription = stringResource(R.string.clear_collection),
                modifier = Modifier.clickable { collections -= current },
            )
        }
    )
}

sealed class Suggestion {
    object Hidden : Suggestion()
    object Suggested : Suggestion()
    object Shown : Suggestion()
}

@Preview
@Composable
fun NoteOptionsPreview() {
    val noteModel = remember { NotePatchViewModel() }
    val viewModel = remember { CollectionViewModel() }
    NoteOptionsScreen(noteModel, viewModel) {}
}