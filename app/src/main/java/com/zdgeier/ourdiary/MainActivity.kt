package com.zdgeier.ourdiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 0
    }

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        loginViewModel.firebaseUser.observe(this, Observer {
            if (it == null) {
                signIn()
            }
            else {
                Log.d(TAG, "Logged in as: ${it.email} ${it.displayName}")
            }
        })
    }

    fun signIn() {
        Log.d(TAG, "Trying to sign in...")

        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            Log.d(TAG, "User signed out...")
            loginViewModel.updateFirebaseUser()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                // If sign-in is successful, update ViewModel.
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Signed in successfully")
                    loginViewModel.updateFirebaseUser()
                } else {
                    Log.d(TAG, "Sign in failed")
                }
            }
            else -> {
                Log.e(TAG, "Unrecognized request code: $requestCode")
            }
        }
    }
}
