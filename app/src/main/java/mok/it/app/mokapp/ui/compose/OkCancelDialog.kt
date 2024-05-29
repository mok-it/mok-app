package mok.it.app.mokapp.ui.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import mok.it.app.mokapp.R

@Composable
fun OkCancelDialog(
        title: String? = null,
        text: String,
        positiveButtonText: String = "OK",
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
) =
        AlertDialog(
                title = {
                    title?.let {
                        Text(it)
                    }
                },
                onDismissRequest = { onDismiss() },
                confirmButton = {
                    Button(
                            onClick = {
                                onConfirm()
                                onDismiss()
                            }) {
                        Text(positiveButtonText)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { onDismiss() }) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                text = {
                    Text(text)
                })