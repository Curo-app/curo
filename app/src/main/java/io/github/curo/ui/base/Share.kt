package io.github.curo.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.SentimentVeryDissatisfied
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.curo.R
import io.github.curo.data.ShareScreenData
import io.github.curo.http.RetrofitBuilder
import io.github.curo.viewmodels.ShareScreenViewModel

@Composable
fun ShareNote(
    viewModel: ShareScreenViewModel,
    onDone: () -> Unit,
    onDismiss: () -> Unit,
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    if (viewModel.link is ShareScreenData.Hidden) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.share_note)) },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = "share icon"
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (val link = viewModel.link) {
                    ShareScreenData.Hidden -> {}
                    ShareScreenData.Error -> {
                        Text(stringResource(R.string.share_error))
                        Spacer(modifier = Modifier.size(10.dp))
                        Icon(
                            imageVector = Icons.Rounded.SentimentVeryDissatisfied,
                            contentDescription = "error",
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    ShareScreenData.Loading -> CircularProgressIndicator()
                    is ShareScreenData.Url -> {
                        Text(stringResource(R.string.share_description))
                        Spacer(modifier = Modifier.size(10.dp))
                        OutlinedTextField(
                            readOnly = true,
                            value = link.url,
                            onValueChange = {},
                            singleLine = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.CopyAll,
                                    contentDescription = stringResource(R.string.copy_to_clipboard),
                                    modifier = Modifier.clickable {
                                        clipboardManager.setText(AnnotatedString(link.url))
                                    },
                                )
                            },
                        )
                    }
                }
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
    val viewModel = remember {
        val x = ShareScreenViewModel(RetrofitBuilder.apiService)
        x.link = ShareScreenData.Url("aboba")
        x
    }
    ShareNote(viewModel, {}, {})
}

@Preview
@Composable
fun SharePreview2() {
    val viewModel = remember {
        val x = ShareScreenViewModel(RetrofitBuilder.apiService)
        x.link = ShareScreenData.Loading
        x
    }
    ShareNote(viewModel, {}, {})
}

@Preview
@Composable
fun SharePreview3() {
    val viewModel = remember {
        val x = ShareScreenViewModel(RetrofitBuilder.apiService)
        x.link = ShareScreenData.Error
        x
    }
    ShareNote(viewModel, {}, {})
}