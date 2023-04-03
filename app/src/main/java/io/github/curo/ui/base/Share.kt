package io.github.curo.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.curo.R
import io.github.curo.data.ShareScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareNote(
    viewModel: ShareScreenViewModel,
    onDone: () -> Unit,
    onDismiss: () -> Unit,
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share note") },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = "share icon"
            )
        },
        text = {
            Column {
                viewModel.link?.let { link ->
                    Text(stringResource(R.string.share_description))
                    OutlinedTextField(
                        readOnly = true,
                        modifier = Modifier.padding(top = 10.dp),
                        value = link,
                        onValueChange = {},
                        singleLine = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.CopyAll,
                                contentDescription = stringResource(R.string.copy_to_clipboard),
                                modifier = Modifier.clickable {
                                    clipboardManager.setText(AnnotatedString(link))
                                },
                            )
                        },
                    )
                } ?: Text(stringResource(R.string.share_error))
            }
        },
        confirmButton = {
            TextButton(onClick = onDone) {
                Text("Done")
            }
        }
    )
}


@Preview
@Composable
fun SharePreview() {
    ShareNote(remember { ShareScreenViewModel() }, {}, {})
}