package io.github.curo.ui.base

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.curo.data.Note
import io.github.curo.ui.theme.CuroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditMenu(
    note: Note,
    onSaveNote: (Note) -> Unit,
    onDiscardNote: () -> Unit,
    onShareNote: () -> Unit,
    onDeleteNode: () -> Unit
) {
    val listState = rememberLazyListState()
    val firstVisibleItemIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }
    val firstVisibleItemScrollOffset by remember {
        derivedStateOf { listState.firstVisibleItemScrollOffset }
    }
    val isBodyUnmoved = firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0

    Scaffold(
        topBar = { TopBar(onDiscardNote, isBodyUnmoved) },
        bottomBar = { BottomBar(note, onSaveNote, onShareNote, onDeleteNode) },
    ) {
        var title by remember { mutableStateOf(note.title) }
        var content by remember { mutableStateOf(note.content) }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TransparentHintTextField(
                    text = title,
                    hint = "Title",
                    onValueChange = { text -> title = text },
                    textStyle = TextStyle(
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize
                    )
                )
            }

            item {
                TransparentHintTextField(
                    modifier = Modifier.fillMaxHeight(),
                    text = content,
                    hint = "Note",
                    onValueChange = { text -> content = text },
                    textStyle = TextStyle(
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                )
            }
        }
    }
}

@Composable
private fun TransparentHintTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    hint: String = "",
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
) {
    Box(
        modifier = modifier
    ) {
        BasicTextField(
            value = text,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface
            ).merge(
                textStyle
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
        if (text.isEmpty()) {
            Text(
                text = hint,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = textStyle
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(onDiscardNote: () -> Unit, isUnmoved: Boolean) {
    Surface(
        tonalElevation = if (isUnmoved) 0.dp else 4.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        TopAppBar(
            title = { Text("") },
            navigationIcon = {
                IconButton(onClick = onDiscardNote) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Discard changes")
                }
            },
        )
    }
}

@Composable
private fun BottomBar(
    note: Note,
    onSaveNote: (Note) -> Unit,
    onShareNote: () -> Unit,
    onDeleteNode: () -> Unit
) {
    BottomAppBar(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onSaveNote(note) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                content = {
                    Icon(
                        imageVector = Icons.Default.Done,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = "Save note",
                    )
                }
            )
        },
        actions = {
            IconButton(onClick = onShareNote) {
                Icon(
                    imageVector = Icons.Default.Share,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Share note"
                )
            }
            IconButton(onClick = onDeleteNode) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Delete note"
                )
            }
        }
    )
}

@Preview
@Composable
fun EditNoteScreenPreview() {
    val note = Note(
        id = 1,
        title = "",
        content = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Habitant morbi tristique senectus et netus. Maecenas pharetra convallis posuere morbi leo urna molestie at. Ac feugiat sed lectus vestibulum. In ornare quam viverra orci sagittis eu volutpat odio. Phasellus faucibus scelerisque eleifend donec pretium. Laoreet id donec ultrices tincidunt arcu non. Et tortor consequat id porta. Sit amet consectetur adipiscing elit ut aliquam purus sit. Amet justo donec enim diam vulputate ut pharetra. Elit at imperdiet dui accumsan sit amet nulla facilisi. Eu sem integer vitae justo. In tellus integer feugiat scelerisque varius morbi enim.

            In hac habitasse platea dictumst quisque sagittis purus. Urna cursus eget nunc scelerisque viverra mauris. Ut faucibus pulvinar elementum integer enim neque volutpat ac. Sed vulputate odio ut enim blandit volutpat maecenas volutpat blandit. Dictumst quisque sagittis purus sit. Vulputate odio ut enim blandit. Turpis egestas integer eget aliquet nibh praesent tristique. Nunc vel risus commodo viverra maecenas accumsan lacus vel. Feugiat nisl pretium fusce id. Vitae justo eget magna fermentum iaculis eu non diam phasellus. Ac felis donec et odio. Lorem dolor sed viverra ipsum nunc aliquet. Massa massa ultricies mi quis hendrerit dolor magna eget. Sagittis vitae et leo duis ut diam. Ipsum faucibus vitae aliquet nec ullamcorper sit amet risus nullam.

            Diam ut venenatis tellus in. Gravida quis blandit turpis cursus in. Dolor magna eget est lorem ipsum dolor sit. Etiam dignissim diam quis enim. Interdum velit euismod in pellentesque massa. Neque aliquam vestibulum morbi blandit cursus risus at ultrices mi. Tristique et egestas quis ipsum suspendisse ultrices gravida. Consequat semper viverra nam libero justo laoreet sit amet cursus. Eget aliquet nibh praesent tristique magna sit amet purus gravida. Amet commodo nulla facilisi nullam vehicula. Turpis massa sed elementum tempus egestas sed sed risus. Duis at tellus at urna condimentum mattis pellentesque. Volutpat maecenas volutpat blandit aliquam etiam erat. Sociis natoque penatibus et magnis.

            Netus et malesuada fames ac turpis egestas maecenas pharetra. Odio ut enim blandit volutpat maecenas volutpat blandit aliquam. Elit ullamcorper dignissim cras tincidunt lobortis feugiat vivamus at augue. Odio eu feugiat pretium nibh ipsum consequat nisl vel. Aliquet porttitor lacus luctus accumsan tortor posuere ac ut. Molestie ac feugiat sed lectus vestibulum mattis ullamcorper velit. Pellentesque habitant morbi tristique senectus et netus et malesuada. Mauris commodo quis imperdiet massa tincidunt nunc pulvinar. Libero enim sed faucibus turpis in eu mi. Et netus et malesuada fames. Risus nullam eget felis eget nunc lobortis. Malesuada fames ac turpis egestas integer eget. Aenean et tortor at risus viverra adipiscing at. Adipiscing enim eu turpis egestas pretium.

            Proin libero nunc consequat interdum varius sit amet. Tincidunt arcu non sodales neque sodales ut etiam sit. Dictum sit amet justo donec. Nec feugiat in fermentum posuere urna nec tincidunt praesent semper. In massa tempor nec feugiat nisl pretium fusce id. Tellus cras adipiscing enim eu turpis egestas pretium aenean. Sit amet mauris commodo quis imperdiet massa tincidunt nunc pulvinar. Duis ut diam quam nulla. Tempus urna et pharetra pharetra massa massa ultricies. Lorem ipsum dolor sit amet consectetur adipiscing elit pellentesque habitant. Quis enim lobortis scelerisque fermentum dui faucibus in ornare. Proin sagittis nisl rhoncus mattis rhoncus. Enim nec dui nunc mattis enim ut. Venenatis tellus in metus vulputate eu scelerisque felis. Consequat id porta nibh venenatis cras sed felis eget. Lacus laoreet non curabitur gravida arcu ac tortor. Duis ut diam quam nulla porttitor massa id. Id faucibus nisl tincidunt eget nullam non nisi est.
            
            Cursus vitae congue mauris rhoncus aenean vel elit scelerisque. At elementum eu facilisis sed. Leo integer malesuada nunc vel risus commodo viverra maecenas. Sit amet risus nullam eget felis eget nunc lobortis. Ipsum faucibus vitae aliquet nec ullamcorper sit. Dignissim diam quis enim lobortis scelerisque fermentum dui faucibus. Mattis enim ut tellus elementum sagittis. Augue ut lectus arcu bibendum. Amet consectetur adipiscing elit duis. Imperdiet nulla malesuada pellentesque elit eget gravida cum.
        """.trimIndent()
    )
    CuroTheme(darkTheme = true) {
        Surface {
            NoteEditMenu(note = note, onSaveNote = {}, onDiscardNote = {}, onShareNote = {}, onDeleteNode = {})
        }
    }
}