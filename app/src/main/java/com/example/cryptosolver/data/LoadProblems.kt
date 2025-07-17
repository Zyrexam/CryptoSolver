package com.example.cryptosolver.data

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LoadProblems(private val context: Context) {
    private val database = FirebaseDatabase.getInstance()
    private val problemsRef = database.getReference("problems")

    fun getProblems(callback: (Map<String, Problem>) -> Unit) {
        problemsRef.get().addOnSuccessListener { snapshot ->
            val problemMap = snapshot.children.associate { child ->
                val problem = child.getValue(Problem::class.java) ?: Problem()
                child.key!! to problem.copy(id = child.key!!)
            }
            callback(problemMap)
        }.addOnFailureListener {
            callback(emptyMap())
        }
    }

    fun initializeSampleDataFromJson() {
        val jsonString = loadJSONFromAsset("problems.json") ?: return
        val type = object : TypeToken<List<Problem>>() {}.type
        val problems: List<Problem> = Gson().fromJson(jsonString, type)

        problems.forEach { problem ->
            problemsRef.child(problem.id).setValue(problem)
        }
    }

    private fun loadJSONFromAsset(filename: String): String? {
        return try {
            val inputStream = context.assets.open(filename)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }
}
