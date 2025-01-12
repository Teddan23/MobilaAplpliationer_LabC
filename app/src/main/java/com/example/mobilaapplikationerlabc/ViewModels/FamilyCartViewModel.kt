package com.example.mobilaapplikationerlabc.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilaapplikationerlabc.Repositories.FamilyCartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FamilyCartViewModel(application: Application) : AndroidViewModel(application) {

    private val familyCartRepository = FamilyCartRepository()

    private val _shoppingListFlow = MutableStateFlow<List<String>>(emptyList())
    val shoppingListFlow: StateFlow<List<String>> get() = _shoppingListFlow

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchShoppingList() {
        viewModelScope.launch {
            _isLoading.value = true
            _shoppingListFlow.value = familyCartRepository.fetchShoppingList()
            _isLoading.value = false
        }
    }

    fun addItem(newItem: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _shoppingListFlow.value = familyCartRepository.addItem(newItem)
            _isLoading.value = false
        }
    }

    fun removeItem(item: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _shoppingListFlow.value = familyCartRepository.removeItem(item)
            _isLoading.value = false
        }
    }
}
