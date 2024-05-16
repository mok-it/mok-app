package mok.it.app.mokapp

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import mok.it.app.mokapp.databinding.ActivityMainBinding
import mok.it.app.mokapp.databinding.NavHeaderBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject.currentUser
import mok.it.app.mokapp.firebase.FirebaseUserObject.refreshCurrentUserAndUserModel
import mok.it.app.mokapp.firebase.service.UserService.updateFcmTokenIfEmptyOrOutdated


class MainActivity : AppCompatActivity() {

    val firestore = Firebase.firestore
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHeaderBinding: NavHeaderBinding
    private val backDrawerCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navHeaderBinding = NavHeaderBinding.bind(binding.navView.getHeaderView(0))
        setContentView(binding.root)
        setupNavigation()
        setupBackPressed()

        getDataFromRemoteConfig()
    }

    /**Get config data from Firebase, e.g. season*/
    private fun getDataFromRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 300
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(binding.toolbar)
        val appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        //Ha a listfragment-re navigálunk, töltődjön újra a fejléc (regisztráció után ez tölti be)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.allProjectsListFragment && currentUser != null) refreshCurrentUserAndUserModel(
                this
            ) {
                loadApp()
            }
        }
        removeBackArrowFromLoginFragment(navController)
        setNavigationItemSelected(navController)
    }

    private fun setNavigationItemSelected(navController: NavController) {
        binding.navView.setNavigationItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it, navController)
            when (it.itemId) {
                R.id.nav_logout -> {
                    logout()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun removeBackArrowFromLoginFragment(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment) {
                binding.toolbar.navigationIcon = null
            }
        }
    }

    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, backDrawerCallback)
        binding.drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerOpened(drawerView: View) {
                backDrawerCallback.isEnabled = true
            }

            override fun onDrawerClosed(drawerView: View) {
                backDrawerCallback.isEnabled = false
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                //Must be overridden but not used
            }

            override fun onDrawerStateChanged(newState: Int) {
                //Must be overridden but not used
            }
        })
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
        updateFcmTokenIfEmptyOrOutdated()
    }

    private fun setHeader() {
        navHeaderBinding.nameText.text = currentUser?.displayName
        navHeaderBinding.emailText.text = currentUser?.email
        navHeaderBinding.refreshButton.setOnClickListener {
            refreshCurrentUserAndUserModel(this) { loadApp() }
        }
        val requestOptions =
            RequestOptions().apply(RequestOptions().transform(CenterCrop(), RoundedCorners(26)))
        Glide.with(this).load(currentUser?.photoUrl).apply(requestOptions.override(250, 250))
            .into(navHeaderBinding.image)
    }

    private fun setMenuVisibility() {
//        val menu = nav_view.menu
//        val adm = menu.findItem(R.id.adminFragment)
//        adm?.isVisible = userModel.admin //admin is empty, we shouldn't show it yet
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, binding.drawerLayout)
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