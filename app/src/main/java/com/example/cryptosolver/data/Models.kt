package com.example.cryptosolver.data

data class Problem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val difficulty: String = "",
    val category: String = "",
    val points: Int = 0,
    val requiredLevel: Int = 1,
    val cipherText: String = "",
    val correctAnswer: String = "",
    val hints: List<String> = emptyList(),
    val icon: String = "🔐",
    val isSolved: Boolean = false
)

data class UserProfile(
    val username: String = "",
    val email: String = "",
    val level: Int = 1,
    val totalPoints: Int = 0,
    val levelProgress: Float = 0f,
    val currentStreak: Int = 0,
    val globalRank: Int = 999,
    val joinDate: String = ""
)

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "",
    val points: Int = 0,
    val dateEarned: String = ""
)

data class SubmissionResult(
    val isCorrect: Boolean,
    val message: String,
    val pointsAwarded: Int,
    val levelUp: Boolean,
    val newLevel: Int
)
