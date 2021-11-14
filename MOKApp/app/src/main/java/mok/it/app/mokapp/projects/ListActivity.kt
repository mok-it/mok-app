package mok.it.app.mokapp.projects

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_main.textView2
import mok.it.app.mokapp.R
import mok.it.app.mokapp.auth.LoginActivity

class ListActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser == null) {
            // TODO handle null user
        } else {
            currentUser = mAuth.currentUser!!
        }

        //Glide.with(this).load(currentUser?.photoUrl).into(imageView)

        /*button.setOnClickListener{
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }*/
    }
}