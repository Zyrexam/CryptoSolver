package com.example.cryptosolver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptosolver.navigation.CryptoSolverNavigation
import com.example.cryptosolver.ui.theme.CryptoSolverTheme
import com.example.cryptosolver.ui.viewmodel.AuthViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            CryptoSolverTheme {
                val authViewModel: AuthViewModel = viewModel()
                CryptoSolverNavigation(authViewModel = authViewModel)
            }
        }
    }

}





