package mok.it.app.mokapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.os.Looper
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
import kotlinx.android.synthetic.main.nav_header.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.auth.LoginActivity
import mok.it.app.mokapp.fragments.*
import mok.it.app.mokapp.interfaces.UserRefreshedListener
import mok.it.app.mokapp.interfaces.UserRefresher
import mok.it.app.mokapp.model.Project
import mok.it.app.mokapp.model.User


class ContainerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, CategoryFragment.ItemClickedListener, UserRefresher, UserRefreshedListener{

    val firestore = Firebase.firestore;
    var hirlevelUrl = "https://drive.google.com/drive/folders/1KJX4tPXiFGN1OTNMZkBqHGswRTVfLPsQ?usp=sharing"
    var feladatUrl = "https://docs.google.com/forms/d/e/1FAIpQLSf4-Pje-gPDa1mVTsVgI2qw37e5u9eJMK1bN3xolIQCJWPHmA/viewform"
    var previousCategory = "Univerzális"
    var previousBadge = ""

    companion object {
        var currentUser = FirebaseAuth.getInstance().currentUser!!
        lateinit var userModel: User
        const val TAG = "ContainerActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
    }

    override fun onResume() {
        super.onResume()
        currentUser = FirebaseAuth.getInstance().currentUser!!
        getUser(currentUser.uid)
    }

    private fun setHeader(){
        nameText.text = currentUser.displayName
        emailText.text = currentUser.email
        refreshButton.setOnClickListener{
            currentUser = FirebaseAuth.getInstance().currentUser!!
            getUser(currentUser.uid)
        }
        val requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(26))
        Glide
            .with(this)
            .load(currentUser?.photoUrl)
            .apply( requestOptions.override(250, 250))
            .into(image)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initialNavigation(){
        nav_view.setNavigationItemSelectedListener(this)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CategoryFragment(this, "Univerzális")).commit()
        nav_view.setCheckedItem(R.id.nav_list)
    }

    private fun removeSpinner(){
        spinner.visibility = View.GONE
    }
    private fun setMenuVisibility(){
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val menu = navView.menu

        //MCS Kategóriák láthatósága
        val it = menu.findItem(R.id.it)
        val ped = menu.findItem(R.id.ped)
        val fel = menu.findItem(R.id.fel)
        val kre = menu.findItem(R.id.kre)
        val gra = menu.findItem(R.id.gra)

        it?.isVisible = userModel.categories.contains("IT")
        ped?.isVisible = userModel.categories.contains("Pedagógia")
        fel?.isVisible = userModel.categories.contains("Feladatsor")
        kre?.isVisible = userModel.categories.contains("Kreatív")
        gra?.isVisible = userModel.categories.contains("Grafika")

        //Admin láthatósága
        val adm = menu.findItem(R.id.admin)
        if (!userModel.admin)
            adm?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_logout) {
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
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
            if (detailsFragment != null && detailsFragment.isVisible) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CategoryFragment(this, previousCategory)).commit()
            }
            else if (commentsFragment != null && commentsFragment.isVisible) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DetailsFragment(previousBadge, this), "DetailsFragment").commit()
            }
            else{
                super.onBackPressed()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_list -> changeCategoryFragment("Univerzális")
            R.id.nav_completed -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MyBadgesFragment(this)).commit()
            R.id.nav_profile -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment(this)).commit()
            R.id.admin -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AdminFragment()).commit()
            R.id.nav_hirlevel -> openLink(hirlevelUrl)
            R.id.nav_feladat -> openLink(feladatUrl)
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

    private fun changeCategoryFragment(category: String){
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CategoryFragment(this, category)).commit()
        previousCategory = category
    }

    private fun openLink(url: String){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun logOut(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getUser(uid: String){
        Firebase.firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                val userToBe = document.toObject(User::class.java)
                Log.d(TAG, "getUser(): got document ${userToBe.toString()}")
                if (userToBe != null) {
                    userModel = userToBe
                    setHeader()
                    setMenuVisibility()
                    removeSpinner()
                    initialNavigation()
                }
                else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        getUser(uid)
                    }, 1000)
                }
            }
    }

    override fun onItemClicked(badgeId: String, category: String) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DetailsFragment(badgeId, this), "DetailsFragment").commit()
        previousCategory = category
        previousBadge = badgeId
    }

    override fun refreshUser(listener: UserRefreshedListener){
        currentUser = FirebaseAuth.getInstance().currentUser!!
        Firebase.firestore.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userModel = document.toObject(User::class.java)!!
                    setHeader()
                    setMenuVisibility()
                    listener.userRefreshed()
                }
            }
    }

    override fun userRefreshed() {
        Firebase.firestore.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userModel = document.toObject(User::class.java)!!
                    setHeader()
                    setMenuVisibility()
                }
            }
    }
}