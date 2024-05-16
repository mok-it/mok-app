package mok.it.app.mokapp.feature.project_comments

import android.icu.text.DateFormat.getDateTimeInstance
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.feature.project_detail.DetailsFragmentArgs
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.firebase.service.CommentService.addComment
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.ui.compose.UserIcon
import mok.it.app.mokapp.ui.compose.theme.MokAppTheme

class CommentsFragment : Fragment() {
    private val args: DetailsFragmentArgs by navArgs()

    private val viewModel: CommentsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                MokAppTheme {
                    CommentsScreen()
                }
            }
        }

    @Composable
    fun CommentsScreen() {
        val comments by viewModel.getComments(args.projectId)
            .observeAsState(initial = emptyList())

        Scaffold(
            bottomBar = {
                var text by remember { mutableStateOf("") }
                CommentInputBox(text,
                    onSendComment = {
                        addComment(
                            args.projectId, Comment(
                                text = text,
                                uid = userModel.documentId,
                                userName = userModel.name
                            )
                        )
                        text = ""
                    },
                    onEditText = {
                        text = it
                    })
            }
        ) { padding ->
            Surface {

                Column {
                    if (comments.isEmpty()) {
                        Text(
                            text = "Itt nincsenek még kommentek. Írj egyet most!",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(comments) { comment ->
                                CommentCard(comment = comment)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CommentCard(comment: Comment) {

        fun navigate(user: User) = findNavController().navigate(
            CommentsFragmentDirections.actionGlobalMemberFragment(user)
        )

        val user by viewModel.getUserById(comment.uid).observeAsState(initial = User())

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserIcon(
                        navController = findNavController(), user = user, modifier = Modifier
                            .padding(8.dp)
                    )

                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navigate(user) },
                    )

                    Text(
                        text = getDateTimeInstance().format(comment.time.toDate()),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End
                    )
                }

                Text(
                    text = comment.text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    @Composable
    fun CommentInputBox(text: String, onEditText: (String) -> Unit, onSendComment: () -> Unit) {
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        Row(
            modifier = Modifier.padding(8.dp)

        ) {
            TextField(
                value = text,
                onValueChange = { onEditText(it) },
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
                    .focusRequester(focusRequester),
            )
            IconButton(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    if (text.isNotEmpty()) {
                        onSendComment()
                        focusManager.clearFocus()
                    }
                }) {
                Icon(Icons.AutoMirrored.Default.Send, contentDescription = "Send Comment")
            }
        }
    }
}