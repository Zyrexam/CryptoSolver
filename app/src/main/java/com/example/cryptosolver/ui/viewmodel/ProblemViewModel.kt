package com.example.cryptosolver.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptosolver.data.LoadProblems
import com.example.cryptosolver.data.Problem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProblemViewModel : ViewModel() {
    private val firebaseHelper = LoadProblems(
        context = TODO()
    )
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val _problems = MutableStateFlow<Map<String, Problem>>(emptyMap())
    val problems: StateFlow<Map<String, Problem>> = _problems

    private val _userLevel = MutableStateFlow(1)
    val userLevel: StateFlow<Int> = _userLevel

    fun fetchProblems() {
        viewModelScope.launch {
            firebaseHelper.getProblems { problemMap ->
                _problems.value = problemMap
            }
        }
    }

    fun fetchUserLevel() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            database.getReference("users").child(userId).child("level").get()
                .addOnSuccessListener { snapshot ->
                    val level = snapshot.getValue(Int::class.java) ?: 1
                    _userLevel.value = level
                }
        }
    }
}
