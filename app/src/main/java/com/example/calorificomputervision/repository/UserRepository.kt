package com.example.calorificomputervision.repository

import com.example.calorificomputervision.data.User
import com.example.calorificomputervision.data.UserDao

class UserRepository(private val userDao: UserDao) {
    suspend fun registerUser(username: String, password: String): Long {
        val user = User(username = username, password = password)
        return userDao.insert(user)
    }

    suspend fun loginUser(username: String, password: String): User? {
        val user = userDao.getUserByUsername(username)
        return if (user?.password == password) user else null
    }
}