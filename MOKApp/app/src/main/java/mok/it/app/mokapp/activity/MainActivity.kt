package mok.it.app.mokapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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


class MainActivity : AppCompatActivity() {

    val firestore = Firebase.firestore
    private lateinit var navController: NavController
    private val mcsArray = arrayOf("IT", "Pedagógia", "Feladatsor", "Kreatív", "Grafika")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(findViewById(R.id.toolbar))
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)

        removeBackArrowFromLoginFragment(navController)
        setNavigationItemSelected(navController)
    }

    private fun setNavigationItemSelected(navController: NavController) {
        nav_view.setNavigationItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it, navController)
            if (it.title in mcsArray) {
                //TODO
                //navigateToBadgesByMCS(it.title.toString())
            } else {
                when (it.itemId) {
                    R.id.nav_logout -> {
                        logout()
                    }
                }
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun removeBackArrowFromLoginFragment(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment) {
                toolbar.navigationIcon = null
            }
        }
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
        val requestOptions =
            RequestOptions().apply(RequestOptions().transform(CenterCrop(), RoundedCorners(26)))
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

//    private fun navigateToBadgesByMCS(category: String) {
////        val action = AllBadgesListFragmentDirections.actionAllBadgesListFragmentToBadgesByMCSFragment(
////            category
////        )
////        findNavController().navigate(action)
//    }

    private fun logout() {
        currentUser = null
        FirebaseAuth.getInstance().signOut()
        GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        )
            .signOut()
            .addOnSuccessListener { navController.navigate(R.id.action_global_loginFragment) }
    }
}

//TODO ha változik a profile pic, az új képet elmenteni
//    private fun updateProfilePic() {
////        val data = hashMapOf(
////            "pictureURL" to user.photoUrl,
////            "uid" to user.uid
////        )
////
////        functions
////            .getHttpsCallable("userLoggedIn")
////            .call(data)
//    }
//}