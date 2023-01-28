package mok.it.app.mokapp.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.FirebaseUserObject.currentUser
import mok.it.app.mokapp.firebase.FirebaseUserObject.refreshCurrentUserAndUserModel
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.fragments.AllBadgesListFragmentDirections


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val firestore = Firebase.firestore
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (FirebaseAuth.getInstance().currentUser != null) refreshCurrentUserAndUserModel(this) {
            loadApp()
        }
    }

    private fun loadApp() {
        setHeader()
        setMenuVisibility()
    }

    private fun setHeader() {
        nameText.text = currentUser?.displayName
        emailText.text = currentUser?.email
        refreshButton.setOnClickListener {
            refreshCurrentUserAndUserModel(this) { loadApp() }
        }
        val requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(26))
        Glide.with(this).load(currentUser?.photoUrl).apply(requestOptions.override(250, 250))
            .into(image)
    }

    private fun setMenuVisibility() {
        val menu = nav_view.menu

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
        val adm = menu.findItem(R.id.adminFragment)
        adm?.isVisible = userModel.admin
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawer_layout)
    }

    //    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        //val navController = findNavController(R.id.nav_host_fragment)d
//        Log.d("mainactivity", "onOptionsItemSelected: entered")
//        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
//    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("asd", "onNavigationItemSelected in allbadgeslistfragment: entered")
        when (item.itemId) {
            //R.id.nav_logout -> logOut()
            //TODO ezt még meg kell csinálni - paramétert átadni a transitionnak, hogy melyik mcs-t kell betölteni
//            R.id.it -> changeCategoryFragment("IT")
//            R.id.fel -> changeCategoryFragment("Feladatsor")
//            R.id.gra -> changeCategoryFragment("Grafika")
//            R.id.kre -> changeCategoryFragment("Kreatív")
//            R.id.ped -> changeCategoryFragment("Pedagógia")
//            //további jövőbeli munkacsoportok hasonlóan
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val action = AllBadgesListFragmentDirections.actionAllBadgesListFragmentToLoginFragment()
        //findNavController().navigate(action)
    }

    //TODO ha változik a profile pic, az új képet elmenteni
    private fun updateProfilePic() {
//        val data = hashMapOf(
//            "pictureURL" to user.photoUrl,
//            "uid" to user.uid
//        )
//
//        functions
//            .getHttpsCallable("userLoggedIn")
//            .call(data)
    }
}