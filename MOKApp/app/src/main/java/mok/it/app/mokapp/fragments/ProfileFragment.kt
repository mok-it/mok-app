package mok.it.app.mokapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.recyclerView
import mok.it.app.mokapp.FirebaseUserObject.currentUser
import mok.it.app.mokapp.FirebaseUserObject.userModel
import mok.it.app.mokapp.R
import mok.it.app.mokapp.activity.MainActivity
import mok.it.app.mokapp.interfaces.UserRefreshedListener
import mok.it.app.mokapp.model.Category
import mok.it.app.mokapp.recyclerview.CategoryNameAdapter
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager

class ProfileFragment(private val listener: UserRefreshedListener) : Fragment() {
    lateinit var categories: ArrayList<Category>
    lateinit var names: Array<String>
    lateinit var checkedNames: BooleanArray
    var selectedCategories: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        modifyButton.setOnClickListener {
            getCategories()
        }
    }

    private fun getCategories(){
        categories = ArrayList()

        Firebase.firestore.collection("categories")
            .whereNotEqualTo("name", "Univerzális")
            .get()
            .addOnSuccessListener{ documents ->
                if (documents != null) {
                    for (document in documents){
                        categories.add(document.toObject(Category::class.java))
                    }
                    names = Array(categories.size){i->categories[i].name}
                    checkedNames = BooleanArray(names.size){false}
                    names.forEachIndexed {index, name ->
                        if (name in userModel.categories){
                            checkedNames[index] = true
                        }
                    }
                    initCategoryDialog()
                }
            }
    }

    private fun initCategoryDialog(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Válaszd ki a munkacsoportjaid!")
        builder.setMultiChoiceItems(names, checkedNames){dialog, which, isChecked ->
            checkedNames[which] = isChecked
        }
        builder.setPositiveButton("Ok"){dialog, which ->
            selectedCategories = ArrayList()
            selectedCategories.add("Univerzális")
            for (i in names.indices){
                if (checkedNames[i]){
                    Log.d("Selected", names[i])
                    selectedCategories.add(names[i])
                }
            }
            updateCategories()
        }
        builder.setNegativeButton("Mégsem"){dialog, which ->
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun updateCategories(){
        val userRef = Firebase.firestore.collection("users").document(currentUser.uid)
        userRef.update("categories", selectedCategories)
        listener.userRefreshed()

        //TODO ez csúnya, összekötni a Firestore-ból visszaérkező adattal?
        recyclerView.adapter = CategoryNameAdapter(selectedCategories)
    }

    private fun initRecyclerView(){
        recyclerView.adapter = CategoryNameAdapter(userModel.categories as ArrayList<String>)
        recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }
}