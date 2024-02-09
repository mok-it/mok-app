package mok.it.app.mokapp.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import mok.it.app.mokapp.R
import mok.it.app.mokapp.databinding.CardProjectBinding
import mok.it.app.mokapp.databinding.FragmentAllProjectsListBinding
import mok.it.app.mokapp.dialog.FilterDialogFragment.Companion.filterResultKey
import mok.it.app.mokapp.firebase.FirebaseUserObject.currentUser
import mok.it.app.mokapp.firebase.FirebaseUserObject.refreshCurrentUserAndUserModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.Filter
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.recyclerview.ProjectViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.utility.Utility.getIconFileName
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


private const val TAG = "AllProjectsListFragment"

class AllProjectsListFragment :
    Fragment() {

    private val args: AllProjectsListFragmentArgs by navArgs()
    private lateinit var filter: Filter
    private lateinit var binding: FragmentAllProjectsListBinding

    private var defaultBackgroundColor: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllProjectsListBinding.inflate(inflater, container, false)
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
        defaultBackgroundColor = typedValue.data
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filter = args.filter ?: Filter()
        setupTopMenu()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Filter>(
            filterResultKey
        )
            ?.observe(
                viewLifecycleOwner
            ) { resultFilter ->
                filter = resultFilter
                initRecyclerView()
            }
        loginOrLoad()
    }

    private fun setupTopMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.add(R.id.filter, R.id.filter, 0, R.string.filters)
                    .setIcon(R.drawable.ic_filter_white)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.filter -> {
                        findNavController().navigate(
                            AllProjectsListFragmentDirections.actionAllProjectsListFragmentToFilterDialogFragment(
                                filter
                            )
                        )
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun loginOrLoad() {
        if (currentUser == null) {
            findNavController().navigate(R.id.action_global_loginFragment)
        } else {
            refreshCurrentUserAndUserModel(requireContext()) {
                initRecyclerView()
            }
        }
    }

    /**
     * Tries to load the image provided into the given view. If that did not
     * succeed, it tries to load the default 'broken' image. If that also
     * fails, leaves the image empty and logs an error message.
     */
    private fun loadImage(imageView: ImageView, imageURL: String, callback: Callback) {
        if (tryLoadingImage(imageView, imageURL, callback)) return
    }

    /**
     * Tries to load an image into the given image view. If for some reason
     * the provided URL does not point to a valid image file, false is returned.
     *
     * @return true if the function succeeded, false if failed
     */
    private fun tryLoadingImage(
        imageView: ImageView,
        imageURL: String,
        callback: Callback
    ): Boolean {
        return try {
            Picasso.get().apply {
                load(imageURL).into(imageView, callback)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getAdapter(): FirestoreRecyclerAdapter<Project, ProjectViewHolder> {
        val query = getFilteredQuery()
        val options =
            FirestoreRecyclerOptions.Builder<Project>().setQuery(query, Project::class.java)
                .setLifecycleOwner(this).build()
        return object : FirestoreRecyclerAdapter<Project, ProjectViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProjectViewHolder (
                CardProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            override fun onBindViewHolder(
                holder: ProjectViewHolder,
                position: Int,
                model: Project
            ) {
                val tvName: TextView = holder.binding.projectName
                val tvDesc: TextView = holder.binding.projectDescription
                val ivImg: ImageView = holder.binding.projectIcon
                val tvMandatory: TextView = holder.binding.mandatoryTextView
                val tvBadgeValue: TextView = holder.binding.projectBadgeValueTextView

                tvName.text =
                    getString(R.string.projectName, model.name, model.categoryEnum)
                tvDesc.text = model.description
                tvMandatory.isVisible = model.mandatory
                tvBadgeValue.text = model.maxBadges.toString()

                val iconFileName = getIconFileName(model.icon)
                val iconFile = File(context?.filesDir, iconFileName)
                if (iconFile.exists()) {
                    Log.i(TAG, "loading badge icon " + iconFile.path)
                    val bitmap: Bitmap = BitmapFactory.decodeFile(iconFile.path)
                    ivImg.setImageBitmap(bitmap)
                } else {
                    Log.i(TAG, "downloading badge icon " + model.icon)
                    val callback = object : Callback {
                        override fun onSuccess() {
                            // save image
                            Log.i(TAG, "saving badge icon " + iconFile.path)
                            val bitmap: Bitmap = ivImg.drawable.toBitmap()
                            val fos: FileOutputStream?
                            try {
                                fos = FileOutputStream(iconFile)
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                                fos.run {
                                    flush()
                                    close()
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onError(e: java.lang.Exception?) {
                            Log.e(TAG, e.toString())
                        }
                    }
                    loadImage(ivImg, model.icon, callback)
                }

                if (userModel.projectBadges.contains(model.id)) {
                    holder.itemView.setBackgroundResource(R.drawable.gradient1)
                }
                else {
                    holder.itemView.setBackgroundColor(defaultBackgroundColor)
                }

                holder.itemView.setOnClickListener {
                    val action =
                        AllProjectsListFragmentDirections.actionAllProjectsListFragmentToDetailsFragment(
                            model.id
                        )
                    findNavController().navigate(action)
                }

                stopShimmer()
            }
        }
    }

    private fun stopShimmer() {
        binding.shimmerFrameLayout.hideShimmer()
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = View.GONE
    }

    private fun initRecyclerView() {
        var adapter = getAdapter()
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = WrapContentLinearLayoutManager(this.context)
        binding.addProjectButton

        binding.addProjectButton.setOnClickListener {
            findNavController().navigate(
                AllProjectsListFragmentDirections.actionAllProjectsListFragmentToCreateProjectFragment(
                    args.category
                )
            )
        }
        setAddProjectButtonVisibility()
        binding.projectSwipeRefresh.setOnRefreshListener {
            // a lehúzás csak az usert tölti újra, a mancsok maguktól frissülnek
            adapter = getAdapter()
            binding.recyclerView.adapter = adapter
            refreshCurrentUserAndUserModel(
                this.requireContext()
            ) { setAddProjectButtonVisibility() }
            binding.projectSwipeRefresh.isRefreshing = false
        }
    }

    private fun getFilteredQuery(): Query {
        //itt szűrünk kategóriákra
        var query =
            Firebase.firestore.collection(Collections.projects)
                .orderBy("created", Query.Direction.DESCENDING)
        if (filter.mandatory) {
            query = query.whereEqualTo("mandatory", true)
        }
        if (filter.joined) {
            query = query.whereArrayContains("members", userModel.documentId)
        }
        if (filter.achieved) {
            query = if (userModel.collectedBadges.isNotEmpty())
                query.whereIn(FieldPath.documentId(), userModel.collectedBadges)
            else
                query.whereEqualTo(FieldPath.documentId(), "An invalid Id")
        }
        if (filter.edited && !filter.joined) { //TODO ideiglenes megoldás, egy query nem tartalmazhat 2 whereArrayContaint-t
            query = query.whereArrayContains("editors", userModel.documentId)
        }
        return query
    }

    private fun setAddProjectButtonVisibility() {
        if (userModel.isCreator || userModel.admin) {
            binding.addProjectButton.visibility = View.VISIBLE
        } else {
            binding.addProjectButton.visibility = View.INVISIBLE
        }
    }
}