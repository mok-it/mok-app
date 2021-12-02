package mok.it.app.mokapp.projects

import android.content.Intent
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_container.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.auth.LoginActivity
import mok.it.app.mokapp.fragments.ListFragment
import mok.it.app.mokapp.fragments.ProfileFragment


class ContainerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mok.it.app.mokapp.R.layout.activity_container)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!

        val header: View = nav_view.getHeaderView(0)
        var nameTextView = header.findViewById(mok.it.app.mokapp.R.id.nameText) as TextView
        nameTextView.setText(currentUser.displayName)
        var emailTextView = header.findViewById(mok.it.app.mokapp.R.id.emailText) as TextView
        emailTextView.setText(currentUser.email)
        var imageView = header.findViewById(mok.it.app.mokapp.R.id.image) as ImageView

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
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ListFragment()).commit()
        nav_view.setCheckedItem(R.id.nav_list)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == mok.it.app.mokapp.R.id.nav_logout) {
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_list -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ListFragment()).commit()
            R.id.nav_profile -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
            R.id.nav_logout -> logOut()
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true;
    }

    fun logOut(){
        mAuth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}