package mok.it.app.mokapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import mok.it.app.mokapp.auth.LoginActivity
import mok.it.app.mokapp.projects.ListActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        Handler().postDelayed({
            if(user != null){
                val listIntent = Intent(this, ListActivity::class.java)
                startActivity(listIntent)
                finish()
            }
            else {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()
            }
        }, 2000)
    }
}