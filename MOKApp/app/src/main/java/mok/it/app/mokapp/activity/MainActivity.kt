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
import kotlinx.android.synthetic.main.activity_main.drawer_layout
import kotlinx.android.synthetic.main.activity_main.nav_view
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.nav_header.emailText
import kotlinx.android.synthetic.main.nav_header.image
import kotlinx.android.synthetic.main.nav_header.nameText
import kotlinx.android.synthetic.main.nav_header.refreshButton
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.FirebaseUserObject.currentUser
import mok.it.app.mokapp.firebase.FirebaseUserObject.refreshCurrentUserAndUserModel


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

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

        //Ha a listfragment-re navigálunk, töltődjön újra a fejléc (regisztráció után ez tölti be)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.allBadgesListFragment && currentUser != null) refreshCurrentUserAndUserModel(
                this
            ) {
                loadApp()
            }
        }
        removeBackArrowFromLoginFragment(navController)
        setNavigationItemSelected(navController)
    }

    private fun setNavigationItemSelected(navController: NavController) {
        nav_view.setNavigationItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it, navController)
            when (it.itemId) {
                R.id.nav_logout -> {
                    logout()
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentUser != null) refreshCurrentUserAndUserModel(this) {
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
//        val menu = nav_view.menu
//        val adm = menu.findItem(R.id.adminFragment)
//        adm?.isVisible = userModel.admin //admin is empty, we shouldn't show it yet
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawer_layout)
    }

    // Declare the launcher at the top of your Activity/Fragment:
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (!isGranted) {
//            // TODO: Inform user that that your app will not show notifications.
//        }
//    }
//    private fun askNotificationPermission() {
//        // This is only necessary for API level >= 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//                PackageManager.PERMISSION_GRANTED
//            ) {
//                // FCM SDK (and your app) can post notifications.
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                // TODO: display an educational UI explaining to the user the features that will be enabled
//                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
//                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//                //       If the user selects "No thanks," allow the user to continue without notifications.
//            } else {
//                // Directly ask for the permission
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
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