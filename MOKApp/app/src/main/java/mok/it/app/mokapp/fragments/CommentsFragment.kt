package mok.it.app.mokapp.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import mok.it.app.mokapp.R
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.recyclerview.CommentViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import java.text.SimpleDateFormat

class CommentsFragment(badgeId: String) : Fragment() {

    val badgeId = badgeId
    val commentsId = "comments"
    private val TAG = "CommentsFragment"
    private lateinit var recyclerView: RecyclerView
    val firestore = Firebase.firestore;
    val projectCollectionPath: String = "/projects";
    val userCollectionPath: String = "/users";
    val formatter = SimpleDateFormat("yyyy.MM.dd. hh:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query = firestore.collection(projectCollectionPath).document(badgeId).collection(commentsId)
        val options = FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment::class.java)
            .setLifecycleOwner(this).build()
        val adapter = object: FirestoreRecyclerAdapter<Comment, CommentViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
                val view = LayoutInflater.from(this@CommentsFragment.context).inflate(R.layout.comment_card, parent, false)
                return CommentViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: CommentViewHolder,
                position: Int,
                model: Comment
            ) {
                val tvSender: TextView = holder.itemView.findViewById(R.id.comment_sender)
                val tvTimestamp: TextView = holder.itemView.findViewById(R.id.comment_timestamp)
                val tvText: TextView = holder.itemView.findViewById(R.id.comment_text)
                val ivImg: ImageView = holder.itemView.findViewById(R.id.comment_icon)
                tvSender.text = model.uid
                tvTimestamp.text = formatter.format(model.time.toDate())
                tvText.text = model.text

                firestore.collection(userCollectionPath).document(model.uid).get().addOnSuccessListener { senderDoc ->
                    if (senderDoc != null) {
                        if (senderDoc.get("name") != null)
                            tvSender.text = senderDoc.get("name") as String

                        if (senderDoc.get("photoRUL") != null)
                            tryLoadingImage(ivImg, senderDoc.get("photoURL") as String)
                        else
                            tryLoadingImage(ivImg, getString(R.string.url_no_image))
                    }
                }

                val commentEditText: EditText = view.findViewById(R.id.comment_input_edit_text)
                val fab: View = view.findViewById(R.id.send_comment_fab)
                fab.setOnClickListener { view ->
                    val comment = Comment(
                        commentsId,
                        commentEditText.text.toString(),
                        Timestamp.now(),
                        FirebaseAuth.getInstance().currentUser!!.uid
                    )

                    firestore.collection(projectCollectionPath).document(badgeId).collection(commentsId)
                        .add(comment)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }

                    commentEditText.text.clear()
                    commentEditText.clearFocus()
                    hideKeyboard();
                }
            }
        }

        recyclerView = view.findViewById(R.id.comments_recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = WrapContentLinearLayoutManager(this.context)
        recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Tries to load an image into the given image view. If for some reason
     * the provided URL does not point to a valid image file, false is returned.
     *
     * @return true if the function succeeded, false if failed
     */
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