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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mok.it.app.mokapp.databinding.CardCommentBinding
import mok.it.app.mokapp.databinding.FragmentCommentsBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Comment
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.CommentViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.utility.Utility.TAG
import mok.it.app.mokapp.utility.Utility.loadImage

class CommentsFragment : Fragment() {
    val formatter: DateFormat = getDateTimeInstance()
    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var _binding: FragmentCommentsBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query =
            Firebase.firestore.collection(Collections.projects).document(args.projectId)
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
                ) = CommentViewHolder(
                    CardCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )

                override fun onBindViewHolder(
                    holder: CommentViewHolder, position: Int, model: Comment
                ) {
                    val tvSender = holder.binding.commentSender
                    val tvTimestamp = holder.binding.commentTimestamp
                    val tvText = holder.binding.commentText
                    val ivImg = holder.binding.commentIcon
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
//                                    holder.itemView.comment_card.setOnClickListener {
                                    holder.binding.root.setOnClickListener {
                                        findNavController().navigate(
                                            CommentsFragmentDirections.actionGlobalMemberFragment(
                                                user
                                            )
                                        )
                                    }
                                }
                            }
                        }

                    binding.commentsRecyclerView.smoothScrollToPosition(0)
                }
            }

        binding.sendCommentFab.setOnClickListener {
            if (binding.commentEditText.text.toString() != "") {
                val comment = Comment(
                    Collections.commentsRelativePath,
                    binding.commentEditText.text.toString(),
                    Timestamp.now(),
                    userModel.documentId,
                    userModel.name,
                )

                Firebase.firestore.collection(Collections.projects).document(args.projectId)
                    .collection(Collections.commentsRelativePath)
                    .add(comment).addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                    }.addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }

                binding.commentEditText.text.clear()
                binding.commentEditText.clearFocus()
                hideKeyboard()
            }
        }

        binding.commentsRecyclerView.adapter = adapter
        val layoutManager = WrapContentLinearLayoutManager(this.context)
            .apply {
                stackFromEnd = true
                reverseLayout = true
            }

        binding.commentsRecyclerView.layoutManager = layoutManager
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