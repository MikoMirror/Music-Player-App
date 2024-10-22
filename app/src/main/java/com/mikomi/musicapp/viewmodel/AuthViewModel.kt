package com.mikomi.musicapp.viewmodel

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val _loginResult = MutableStateFlow<Result<Unit>?>(null)
    val loginResult: StateFlow<Result<Unit>?> = _loginResult.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
                _currentUser.value = firebaseAuth.currentUser
            }
        }
    }

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            try {
                if (!isNetworkAvailable(activity)) {
                    _loginResult.value = Result.failure(Exception("No internet connection"))
                    return@launch
                }

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("879750150088-aqjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj.apps.googleusercontent.com")
                    .requestEmail()
                    .build()

                val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(activity, gso)
                val signInIntent = googleSignInClient.signInIntent
                activity.startActivityForResult(signInIntent, RC_SIGN_IN)
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }

    fun handleGoogleSignInResult(idToken: String) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
                _currentUser.value = authResult.user
                _loginResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        _currentUser.value = null
        _loginResult.value = null
    }

    fun setLoginResult(result: Result<Unit>) {
        _loginResult.value = result
    }

    companion object {
        const val RC_SIGN_IN = 9001
    }
}
