@file:Suppress("DEPRECATION")

package mok.it.app.mokapp.feature.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import mok.it.app.mokapp.R
import mok.it.app.mokapp.databinding.FragmentLoginBinding
import mok.it.app.mokapp.firebase.FirebaseUserObject.currentUser
import mok.it.app.mokapp.firebase.FirebaseUserObject.refreshCurrentUserAndUserModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleAuth: ActivityResultLauncher<Intent>

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initializeAuth() {
        FirebaseApp.initializeApp(requireContext())

        googleSignInClient = GoogleSignIn.getClient(
                requireContext(), GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAuth()
        if (currentUser != null) {
            navigateAuthUser()
            return
        }

        googleAuth =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val googleSignInResult: GoogleSignInResult? = result.data?.let {
                            Auth.GoogleSignInApi.getSignInResultFromIntent(
                                    it
                            )
                        }
                        if (googleSignInResult!!.isSuccess) {
                            val idToken = googleSignInResult.signInAccount?.idToken
                            FirebaseAuth.getInstance()
                                    .signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                                    .addOnCompleteListener {
                                        navigateAuthUser()
                                    }
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Sikertelen bejelentkezés :(",
                                    Toast.LENGTH_SHORT
                            )
                                    .show()
                        }
                    }
                }

        binding.signInButton.setOnClickListener {
            googleAuth.launch(googleSignInClient.signInIntent)
        }
    }

    private fun navigateAuthUser() {
        refreshCurrentUserAndUserModel(requireContext())
        {
            findNavController().navigate(R.id.action_loginFragment_to_allProjectsListFragment)
        }
    }
}