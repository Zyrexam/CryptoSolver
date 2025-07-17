package com.example.cryptosolver.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cryptosolver.ui.viewmodel.UserViewModel

@Composable
fun MainScreen(
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()

    // Get current destination to manage tab selection
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
                    onClick = {
                        navController.navigate("home") {
                            // Pop up to the start destination to avoid building up a large stack
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when reselecting
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Problems") },
                    label = { Text("Problems") },
                    selected = currentDestination?.hierarchy?.any { it.route == "problems" } == true,
                    onClick = {
                        navController.navigate("problems") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
                    onClick = {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
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

            composable("problems") {
                ProblemListScreen(
                    onNavigateToProblem = { problemId ->
                        navController.navigate("problem_detail/$problemId")
                    }
                )
            }

            // Fixed the route pattern - was "problem/{problemId}" should be "problem_detail/{problemId}"
            composable("problem_detail/{problemId}") { backStackEntry ->
                val problemId = backStackEntry.arguments?.getString("problemId") ?: ""
                ProblemDetailScreen(
                    problemId = problemId,
                    onNavigateBack = { navController.popBackStack() },
                    onProblemSolved = { /* maybe go back or update state */ }
                )
            }

            composable("daily_challenge") {
                DailyChallengeScreen(
                    onDone = { navController.popBackStack() }
                )
            }

            composable("profile") {
                ProfileScreen(onSignOut = onSignOut)
            }
        }
    }
}