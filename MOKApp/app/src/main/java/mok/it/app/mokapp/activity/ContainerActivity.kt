package mok.it.app.mokapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Debug
import android.telecom.Call
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_container.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.auth.LoginActivity
import mok.it.app.mokapp.fragments.*
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User


class ContainerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, CategoryFragment.ItemClickedListener  {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    lateinit var userModel: User
    val firestore = Firebase.firestore;
    var hirlevelUrl = "https://drive.google.com/drive/folders/1KJX4tPXiFGN1OTNMZkBqHGswRTVfLPsQ?usp=sharing"
    var feladatUrl = "https://docs.google.com/forms/d/e/1FAIpQLSf4-Pje-gPDa1mVTsVgI2qw37e5u9eJMK1bN3xolIQCJWPHmA/viewform"
    var previousCategory = "Univerzális"
    var previousBadge = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!

        val header: View = nav_view.getHeaderView(0)
        var nameTextView = header.findViewById(R.id.nameText) as TextView
        nameTextView.setText(currentUser.displayName)
        var emailTextView = header.findViewById(R.id.emailText) as TextView
        emailTextView.setText(currentUser.email)
        var imageView = header.findViewById(R.id.image) as ImageView

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(26))
        Glide
            .with(this)
            .load(currentUser?.photoUrl)
            .apply( requestOptions.override(250, 250))
            .into(imageView)

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        var toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CategoryFragment(this, "Univerzális")).commit()
        nav_view.setCheckedItem(R.id.nav_list)
        getUser(currentUser.uid)
    }

    fun setMenuVisibility(){
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val menu = navView.menu

        val it = menu.findItem(R.id.it)
        val ped = menu.findItem(R.id.ped)
        val fel = menu.findItem(R.id.fel)
        val kre = menu.findItem(R.id.kre)
        val gra = menu.findItem(R.id.gra)

        if (!userModel.categories.contains("IT"))
            it?.setVisible(false)
        if (!userModel.categories.contains("Pedagógia"))
            ped?.setVisible(false)
        if (!userModel.categories.contains("Feladatsor"))
            fel?.setVisible(false)
        if (!userModel.categories.contains("Kreatív"))
            kre?.setVisible(false)
        if (!userModel.categories.contains("Grafika"))
            gra?.setVisible(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_logout) {
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else {
            val detailsFragment: DetailsFragment? =
                supportFragmentManager.findFragmentByTag("DetailsFragment") as DetailsFragment?
            val commentsFragment: CommentsFragment? =
                supportFragmentManager.findFragmentByTag("CommentsFragment") as CommentsFragment?
            if (detailsFragment != null && detailsFragment.isVisible()) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CategoryFragment(this, previousCategory)).commit()
            }
            else if (commentsFragment != null && commentsFragment.isVisible()) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DetailsFragment(previousBadge), "DetailsFragment").commit()
            }
            else{
                super.onBackPressed()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_list -> changeCategoryFragment("Univerzális")
            R.id.nav_completed -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MyBadgesFragment()).commit()
            R.id.nav_profile -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
            R.id.nav_hirlevel -> openHirlevel()
            R.id.nav_feladat -> openFeladat()
            R.id.nav_logout -> logOut()
            R.id.it -> changeCategoryFragment("IT")
            R.id.fel -> changeCategoryFragment("Feladatsor")
            R.id.gra -> changeCategoryFragment("Grafika")
            R.id.kre -> changeCategoryFragment("Kreatív")
            R.id.ped -> changeCategoryFragment("Pedagógia")
            //további jövőbeli munkacsoportok hasonlóan
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true;
    }

    fun changeCategoryFragment(category: String){
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CategoryFragment(this, category)).commit()
        previousCategory = category
    }

    fun openHirlevel(){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(hirlevelUrl)
        startActivity(intent)
    }

    fun openFeladat(){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(feladatUrl)
        startActivity(intent)
    }

    fun logOut(){
        mAuth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun getUser(uid: String) {
        Firebase.firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userModel = document.toObject(User::class.java)!!
                    setMenuVisibility()
                }
            }
    }

    override fun onItemClicked(badgeId: String, category: String) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DetailsFragment(badgeId), "DetailsFragment").commit()
        previousCategory = category
        previousBadge = badgeId
    }
}