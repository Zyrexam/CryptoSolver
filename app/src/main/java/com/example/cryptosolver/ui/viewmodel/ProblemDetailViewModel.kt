package com.example.cryptosolver.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptosolver.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProblemDetailViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val _problem = MutableStateFlow<Problem?>(null)
    val problem: StateFlow<Problem?> = _problem

    private val _userAnswer = MutableStateFlow("")
    val userAnswer: StateFlow<String> = _userAnswer

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _submissionResult = MutableStateFlow<SubmissionResult?>(null)
    val submissionResult: StateFlow<SubmissionResult?> = _submissionResult

    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog: StateFlow<Boolean> = _showSuccessDialog

    fun loadProblem(problemId: String) {
        viewModelScope.launch {
            database.getReference("problems").child(problemId).get()
                .addOnSuccessListener { snapshot ->
                    val problem = snapshot.getValue(Problem::class.java)?.copy(
                        id = problemId
                    )
                    _problem.value = problem
                }
        }
    }

    fun updateAnswer(answer: String) {
        _userAnswer.value = answer
    }

    fun submitAnswer() {
        viewModelScope.launch {
            val currentProblem = _problem.value ?: return@launch
            val userId = auth.currentUser?.uid ?: return@launch

            _isSubmitting.value = true

            // Check if answer is correct
            val isCorrect = _userAnswer.value.trim().lowercase() ==
                    currentProblem.correctAnswer.trim().lowercase()

            if (isCorrect) {
                // Award points and update user progress
                awardPoints(currentProblem, userId)
            } else {
                _submissionResult.value = SubmissionResult(
                    isCorrect = false,
                    message = "Incorrect answer. Try again!",
                    pointsAwarded = 0,
                    levelUp = false,
                    newLevel = 0
                )
            }

            _isSubmitting.value = false
        }
    }

    private fun awardPoints(problem: Problem, userId: String) {
        viewModelScope.launch {
            // Get current user data
            database.getReference("users").child(userId).get()
                .addOnSuccessListener { snapshot ->
                    val currentProfile = snapshot.getValue(UserProfile::class.java) ?: UserProfile()

                    val newPoints = currentProfile.totalPoints + problem.points
                    val newLevel = calculateLevel(newPoints)
                    val levelUp = newLevel > currentProfile.level

                    // Update user profile
                    val updatedProfile = currentProfile.copy(
                        totalPoints = newPoints,
                        level = newLevel,
                        levelProgress = calculateLevelProgress(newPoints, newLevel)
                    )

                    database.getReference("users").child(userId).setValue(updatedProfile)

                    // Mark problem as solved
                    database.getReference("user_solved_problems").child(userId)
                        .child(problem.id).setValue(true)

                    // Create achievement if level up
                    if (levelUp) {
                        createLevelUpAchievement(userId, newLevel)
                    }

                    _submissionResult.value = SubmissionResult(
                        isCorrect = true,
                        message = "Correct! Well done!",
                        pointsAwarded = problem.points,
                        levelUp = levelUp,
                        newLevel = newLevel
                    )
                }
        }
    }

    private fun calculateLevel(points: Int): Int {
        return (points / 100) + 1
    }

    private fun calculateLevelProgress(points: Int, level: Int): Float {
        val pointsInCurrentLevel = points % 100
        return pointsInCurrentLevel / 100f
    }

    private fun createLevelUpAchievement(userId: String, newLevel: Int) {
        val achievement = Achievement(
            id = "level_$newLevel",
            title = "Level $newLevel Reached!",
            description = "You've reached level $newLevel",
            icon = "🎊",
            points = 50,
            dateEarned = System.currentTimeMillis().toString()
        )

        database.getReference("user_achievements").child(userId)
            .child(achievement.id).setValue(achievement)
    }

    fun showSuccessDialog() {
        _showSuccessDialog.value = true
    }

    fun hideSuccessDialog() {
        _showSuccessDialog.value = false
    }
}
