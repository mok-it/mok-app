package mok.it.app.mokapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import mok.it.app.mokapp.R
import mok.it.app.mokapp.activity.ContainerActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser


        Handler().postDelayed({
            if(user != null){
                val contIntent = Intent(this, ContainerActivity::class.java)
                startActivity(contIntent)
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