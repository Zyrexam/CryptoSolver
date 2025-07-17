package com.example.cryptosolver.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cryptosolver.ui.screens.*
import com.example.cryptosolver.ui.viewmodel.AuthViewModel
import com.example.cryptosolver.ui.viewmodel.UserViewModel

@Composable
fun CryptoSolverNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel = viewModel()

) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "main" else "auth"
    ) {
        composable("auth") {
            AuthScreen(
                authViewModel = authViewModel,
                onAuthSuccess = {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate("auth") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

        composable("problem_detail/{problemId}") { backStackEntry ->
            val problemId = backStackEntry.arguments?.getString("problemId") ?: ""
            ProblemDetailScreen(
                problemId = problemId,
                onNavigateBack = { navController.popBackStack() },
                onProblemSolved = { navController.popBackStack() }
            )
        }


        composable("main") {
            HomeScreen(
                userViewModel = userViewModel,
                onNavigateToProblem = { problemId ->
                    navController.navigate("problem_detail/$problemId")
                },
                onNavigateToDailyChallenge = {
                    navController.navigate("daily_challenge")
                }
            )
        }

        composable("problem_detail/{problemId}") { backStackEntry ->
            val problemId = backStackEntry.arguments?.getString("problemId") ?: ""
            ProblemDetailScreen(
                problemId = problemId,
                onNavigateBack = { navController.popBackStack() },
                onProblemSolved = { navController.popBackStack() }
            )
        }

        // Add this new composable
        composable("daily_challenge") {
            DailyChallengeScreen(onDone = {
                // Navigate back or show a success message
                navController.popBackStack()
            })
        }

    }
}