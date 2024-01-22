package mok.it.app.mokapp.fragments

import android.app.Activity
import android.content.Context
import android.icu.text.DateFormat
import android.icu.text.DateFormat.getDateTimeInstance
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.card_comment.view.comment_card
import kotlinx.android.synthetic.main.card_comment.view.comment_icon
import kotlinx.android.synthetic.main.card_comment.view.comment_sender
import kotlinx.android.synthetic.main.card_comment.view.comment_text
import kotlinx.android.synthetic.main.card_comment.view.comment_timestamp
import kotlinx.android.synthetic.main.fragment_comments.commentEditText
import kotlinx.android.synthetic.main.fragment_comments.commentsRecyclerView
import kotlinx.android.synthetic.main.fragment_comments.send_comment_fab
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.CommentViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.utility.Utility.loadImage

class CommentsFragment : Fragment() {
    companion object {
        const val TAG = "CommentsFragment"
    }

    val formatter: DateFormat = getDateTimeInstance()
    private val args: DetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query =
            Firebase.firestore.collection(Collections.projects).document(args.badgeId)
                .collection(Collections.commentsRelativePath)
                .orderBy("time", Query.Direction.DESCENDING)
        val options =
            FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment::class.java)
                .setLifecycleOwner(this).build()
        val adapter =
            object : FirestoreRecyclerAdapter<Comment, CommentViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): CommentViewHolder {
                    val itemView = LayoutInflater.from(this@CommentsFragment.context)
                        .inflate(R.layout.card_comment, parent, false)
                    return CommentViewHolder(itemView)
                }

                override fun onBindViewHolder(
                    holder: CommentViewHolder, position: Int, model: Comment
                ) {
                    val tvSender = holder.itemView.comment_sender
                    val tvTimestamp = holder.itemView.comment_timestamp
                    val tvText = holder.itemView.comment_text
                    val ivImg = holder.itemView.comment_icon
                    tvSender.text = model.uid
                    tvTimestamp.text = formatter.format(model.time.toDate())
                    tvText.text = model.text

                    Firebase.firestore.collection(Collections.users).document(model.uid).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val user: User? = document.toObject(User::class.java)

                                tvSender.text = user?.name
                                loadImage(ivImg, user?.photoURL, requireContext())

                                user?.let {
                                    holder.itemView.comment_card.setOnClickListener {
                                        findNavController().navigate(
                                            CommentsFragmentDirections.actionGlobalMemberFragment(
                                                user
                                            )
                                        )
                                    }
                                }
                            }
                        }

                    commentsRecyclerView.smoothScrollToPosition(0)
                }
            }

        send_comment_fab.setOnClickListener {
            if (commentEditText.text.toString() != "") {
                val comment = Comment(
                    Collections.commentsRelativePath,
                    commentEditText.text.toString(),
                    Timestamp.now(),
                    FirebaseAuth.getInstance().currentUser!!.uid
                )

                Firebase.firestore.collection(Collections.projects).document(args.badgeId)
                    .collection(Collections.commentsRelativePath)
                    .add(comment).addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                    }.addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }

                commentEditText.text.clear()
                commentEditText.clearFocus()
                hideKeyboard()
            }
        }

        commentsRecyclerView.adapter = adapter
        val layoutManager = WrapContentLinearLayoutManager(this.context)
            .apply {
                stackFromEnd = true
                reverseLayout = true
            }

        commentsRecyclerView.layoutManager = layoutManager
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}