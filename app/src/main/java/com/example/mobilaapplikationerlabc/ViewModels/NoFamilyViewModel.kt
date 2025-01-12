package com.example.mobilaapplikationerlabc.ViewModels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilaapplikationerlabc.Repositories.NoFamilyRepository
import kotlinx.coroutines.launch

class NoFamilyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NoFamilyRepository()

    // Funktion för att skapa en ny familj
    fun createFamily(name: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.createFamily(name)
            onComplete(success)
        }
    }

    // Funktion för att gå med i en existerande familj
    fun joinFamily(familyId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.joinFamily(familyId)
            onComplete(success)
        }
    }
}
