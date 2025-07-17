package com.example.cryptosolver.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptosolver.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile

    private val _availableProblems = MutableStateFlow<List<Problem>>(emptyList())
    val availableProblems: StateFlow<List<Problem>> = _availableProblems

    private val _recentAchievements = MutableStateFlow<List<Achievement>>(emptyList())
    val recentAchievements: StateFlow<List<Achievement>> = _recentAchievements

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements

    private val _solvedProblems = MutableStateFlow<List<Problem>>(emptyList())
    val solvedProblems: StateFlow<List<Problem>> = _solvedProblems

    fun loadUserData() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            // Load user profile
            database.getReference("users").child(userId).get()
                .addOnSuccessListener { snapshot ->
                    val profile = snapshot.getValue(UserProfile::class.java) ?: UserProfile(
                        username = auth.currentUser?.email?.substringBefore("@") ?: "User",
                        email = auth.currentUser?.email ?: "",
                        level = 1,
                        totalPoints = 0,
                        levelProgress = 0f,
                        currentStreak = 0,
                        globalRank = 999
                    )
                    _userProfile.value = profile
                }

            // Load available problems based on user level
            loadAvailableProblems()
            loadRecentAchievements()
        }
    }

    private fun loadAvailableProblems() {
        viewModelScope.launch {
            val userLevel = _userProfile.value.level

            database.getReference("problems").get()
                .addOnSuccessListener { snapshot ->
                    val problems = snapshot.children.mapNotNull { child ->
                        child.getValue(Problem::class.java)?.copy(
                            id = child.key ?: ""
                        )
                    }.filter { it.requiredLevel <= userLevel + 1 }

                    _availableProblems.value = problems
                }
        }
    }

    private fun loadRecentAchievements() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            database.getReference("user_achievements").child(userId)
                .orderByChild("dateEarned")
                .limitToLast(5)
                .get()
                .addOnSuccessListener { snapshot ->
                    val achievements = snapshot.children.mapNotNull { child ->
                        child.getValue(Achievement::class.java)
                    }.reversed()

                    _recentAchievements.value = achievements
                }
        }
    }

    fun loadAchievements() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            database.getReference("user_achievements").child(userId).get()
                .addOnSuccessListener { snapshot ->
                    val achievements = snapshot.children.mapNotNull { child ->
                        child.getValue(Achievement::class.java)
                    }

                    _achievements.value = achievements
                }
        }
    }

    fun loadSolvedProblems() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            database.getReference("user_solved_problems").child(userId).get()
                .addOnSuccessListener { snapshot ->
                    val problemIds = snapshot.children.map { it.key ?: "" }

                    // Load problem details for solved problems
                    database.getReference("problems").get()
                        .addOnSuccessListener { problemsSnapshot ->
                            val solvedProblems = problemsSnapshot.children.mapNotNull { child ->
                                if (problemIds.contains(child.key)) {
                                    child.getValue(Problem::class.java)?.copy(
                                        id = child.key ?: "",
                                        isSolved = true
                                    )
                                } else null
                            }

                            _solvedProblems.value = solvedProblems
                        }
                }
        }
    }
}
