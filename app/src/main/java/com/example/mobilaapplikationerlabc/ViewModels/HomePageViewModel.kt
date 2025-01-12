package com.example.mobilaapplikationerlabc.ViewModels

import androidx.lifecycle.ViewModel
import com.example.mobilaapplikationerlabc.Repositories.HomeRepository

class HomePageViewModel : ViewModel() {

    private val homeRepository = HomeRepository()

    val isUserLoggedIn: Boolean
        get() = homeRepository.isUserLoggedIn

    fun getUserName(): String {
        return homeRepository.getUserName()
    }

    fun getEmail(): String {
        return homeRepository.getEmail()
    }

    fun checkIfInFamily(onComplete: (Boolean) -> Unit) {
        homeRepository.checkIfInFamily(onComplete)
    }

    fun addUserToDbIfNeeded() {
        homeRepository.addUserToDbIfNeeded()
    }

    fun signOut() {
        homeRepository.signOut()
    }
}
