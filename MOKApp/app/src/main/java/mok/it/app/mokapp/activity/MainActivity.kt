package mok.it.app.mokapp.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_container.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import mok.it.app.mokapp.FirebaseUserObject.currentUser
import mok.it.app.mokapp.FirebaseUserObject.refreshCurrentUserAndUserModel
import mok.it.app.mokapp.FirebaseUserObject.userModel
import mok.it.app.mokapp.R
import mok.it.app.mokapp.fragments.AllBadgesListFragment
import mok.it.app.mokapp.interfaces.UserRefreshedListener
import mok.it.app.mokapp.interfaces.UserRefresher
import mok.it.app.mokapp.model.User


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AllBadgesListFragment.ItemClickedListener, UserRefresher, UserRefreshedListener {

    val firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // a fallback navigation listener is might needed here
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<NavigationView>(R.id.nav_view)
            .setupWithNavController(navController)
    }

    private fun loadApp() {
        setHeader()
        setMenuVisibility()
    }

    override fun onResume() {
        super.onResume()
        if (FirebaseAuth.getInstance().currentUser != null)
            refreshCurrentUserAndUserModel(this)
            {
                loadApp()
            }
    }

    private fun setHeader() {
        nameText.text = currentUser.displayName
        emailText.text = currentUser.email
        refreshButton.setOnClickListener {
            refreshCurrentUserAndUserModel(this) { loadApp() }
        }
        val requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(26))
        Glide
            .with(this)
            .load(currentUser.photoUrl)
            .apply(requestOptions.override(250, 250))
            .into(image)

        // TODO ?
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        val toggle = ActionBarDrawerToggle(
//            this, drawer_layout, toolbar,
//            R.string.navigation_drawer_open, R.string.navigation_drawer_close
//        )
//        drawer_layout.addDrawerListener(toggle)
//        toggle.syncState()
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
        val adm = menu.findItem(R.id.admin)
        adm?.isVisible = userModel.admin
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)

//        if (item.itemId == R.id.nav_logout) {
//            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
//        }
//
//        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> logOut()
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

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        // TODO
//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
//        finish()
    }

    override fun onItemClicked(badgeId: String, category: String) {
        // TODO open the details fragment with the badgeId
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, DetailsFragment(badgeId, this), "DetailsFragment")
//            .commit()
//        previousCategory = category
//        previousBadge = badgeId
    }

    override fun refreshUser(listener: UserRefreshedListener) {
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