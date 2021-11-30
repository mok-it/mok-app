package mok.it.app.mokapp.projects

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_container.*


class ContainerActivity : AppCompatActivity() {

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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == mok.it.app.mokapp.R.id.nav_logout) {
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT)
        }

        return super.onOptionsItemSelected(item)
    }
}