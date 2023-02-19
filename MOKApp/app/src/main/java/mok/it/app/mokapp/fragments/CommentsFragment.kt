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
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_comments.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.baseclasses.BaseFireFragment
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.recyclerview.CommentViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager

class CommentsFragment : BaseFireFragment() {
    companion object {
        const val TAG = "CommentsFragment"
    }

    private val commentsId = "comments"

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
            firestore.collection(projectCollectionPath).document(args.badgeId)
                .collection(commentsId)
                .orderBy("time", Query.Direction.DESCENDING)
        val options =
            FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment::class.java)
                .setLifecycleOwner(this).build()
        val adapter = object : FirestoreRecyclerAdapter<Comment, CommentViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
                val itemView = LayoutInflater.from(this@CommentsFragment.context)
                    .inflate(R.layout.card_comment, parent, false)
                return CommentViewHolder(itemView)
            }

            override fun onBindViewHolder(
                holder: CommentViewHolder, position: Int, model: Comment
            ) {
                val tvSender: TextView = holder.itemView.findViewById(R.id.comment_sender)
                val tvTimestamp: TextView = holder.itemView.findViewById(R.id.comment_timestamp)
                val tvText: TextView = holder.itemView.findViewById(R.id.comment_text)
                val ivImg: ImageView = holder.itemView.findViewById(R.id.comment_icon)
                tvSender.text = model.uid
                tvTimestamp.text = formatter.format(model.time.toDate())
                tvText.text = model.text

                firestore.collection(userCollectionPath).document(model.uid).get()
                    .addOnSuccessListener { senderDoc ->
                        if (senderDoc != null) {
                            if (senderDoc.get("name") != null) tvSender.text =
                                senderDoc.get("name") as String

                            if (senderDoc.get("photoURL") != null) tryLoadingImage(
                                ivImg, senderDoc.get("photoURL") as String
                            )
                            else tryLoadingImage(ivImg, getString(R.string.url_no_image))
                        }
                    }
                commentsRecyclerView.smoothScrollToPosition(0)
            }
        }

        send_comment_fab.setOnClickListener {
            if (commentEditText.text.toString() != "") {
                val comment = Comment(
                    commentsId,
                    commentEditText.text.toString(),
                    Timestamp.now(),
                    FirebaseAuth.getInstance().currentUser!!.uid
                )

                firestore.collection(projectCollectionPath).document(args.badgeId)
                    .collection(commentsId)
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

    private fun tryLoadingImage(imageView: ImageView, imageURL: String): Boolean {
        return try {
            Picasso.get().apply {
                load(imageURL).into(imageView)
            }
            true
        } catch (e: Exception) {
            Log.w(TAG, "Image not found: $imageURL")
            Log.w(TAG, "Picasso message: " + e.message)
            false
        }
    }
}