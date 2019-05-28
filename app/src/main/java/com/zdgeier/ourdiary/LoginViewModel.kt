package com.zdgeier.ourdiary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel() {
    val firebaseUser = MutableLiveData<FirebaseUser>()

    init {
        updateFirebaseUser()
    }

    fun updateFirebaseUser() {
        val newUser = FirebaseAuth.getInstance().currentUser
        firebaseUser.postValue(newUser)
    }

    fun isSignedIn() = firebaseUser.value != null
}