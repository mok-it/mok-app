package mok.it.app.mokapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.FirebaseUserObject.currentUser
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Category
import mok.it.app.mokapp.model.Category.Companion.toCategory
import mok.it.app.mokapp.recyclerview.CategoryNameAdapter
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager


class ProfileFragment : Fragment() {
    private lateinit var names: MutableList<String>
    private lateinit var checkedNames: BooleanArray
    private var selectedCategories: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        loadProfileFragment()
        modifyButton.setOnClickListener {
            getCategories()
        }
    }

    private fun getCategories() {
        names = Category.values().map { it.toString() }.toMutableList()
        names.remove(Category.UNIVERZALIS.toString())
        checkedNames =
            BooleanArray(names.size) { i -> names[i] in userModel.categoryList.map { it.toString() } }

        initCategoryDialog()
    }

    private fun initCategoryDialog() {
        AlertDialog.Builder(context)
            .setTitle("Válaszd ki a munkacsoportjaid!")
            .setMultiChoiceItems(names.toTypedArray(), checkedNames) { _, which, isChecked ->
                checkedNames[which] = isChecked
            }
            .setPositiveButton("Ok") { _, _ ->
                selectedCategories =
                    names.filterIndexed { index, _ -> checkedNames[index] }.toMutableList()
                selectedCategories.add(Category.UNIVERZALIS.toString())
                updateCategories()
            }
            .setNegativeButton("Mégsem") { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    private fun updateCategories() {
        val userRef = Firebase.firestore.collection("users").document(currentUser!!.uid)
        userRef.update("categories", selectedCategories)
        userModel.categoryList = selectedCategories.map { it.toCategory() }.toMutableList()

        //TODO ez csúnya, összekötni a Firestore-ból visszaérkező adattal?
        recyclerView.adapter = CategoryNameAdapter(selectedCategories)
    }

    private fun initRecyclerView() {
        // TODO use FirestoreRecyclerAdapter instead
        recyclerView.adapter = CategoryNameAdapter(userModel.categoryList.map { it.toString() })
        recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }

    private fun loadProfileFragment(){
        val childFragment: Fragment = MemberFragment()
        val bundle = Bundle()
        bundle.putParcelable("user", userModel)
        childFragment.arguments = bundle
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, childFragment).commit()
    }
}