package com.example.mobilaapplikationerlabc.ViewModels

import androidx.lifecycle.ViewModel
import com.example.mobilaapplikationerlabc.Models.HomeModel

class HomePageViewModel : ViewModel() {

    private val homeModel = HomeModel()

    val isUserLoggedIn: Boolean
        get() = homeModel.isUserLoggedIn

    fun getUserName(): String {
        return homeModel.getUserName()
    }

    fun getEmail(): String {
        return homeModel.getEmail()
    }

    fun checkIfInFamily(onComplete: (Boolean) -> Unit) {
        homeModel.checkIfInFamily(onComplete)
    }

    fun addUserToDbIfNeeded() {
        homeModel.addUserToDbIfNeeded()
    }

    fun signOut() {
        homeModel.signOut()
    }
}
