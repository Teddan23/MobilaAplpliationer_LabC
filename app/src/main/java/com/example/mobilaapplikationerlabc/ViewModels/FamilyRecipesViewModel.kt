package com.example.mobilaapplikationerlabc.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilaapplikationerlabc.DataClasses.SimpleMeal
import com.example.mobilaapplikationerlabc.Repositories.FamilyRecipesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FamilyRecipesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FamilyRecipesRepository()

    private val _recipesFlow = MutableStateFlow<List<SimpleMeal>>(emptyList())
    val recipesFlow: StateFlow<List<SimpleMeal>> = _recipesFlow.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchFamilyRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            _recipesFlow.value = repository.getFamilyRecipes()
            _isLoading.value = false
        }
    }
}
