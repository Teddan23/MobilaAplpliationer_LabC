package com.example.mobilaapplikationerlabc.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilaapplikationerlabc.DataClasses.Family
import com.example.mobilaapplikationerlabc.Repositories.FamilyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FamilyViewModel(application: Application) : AndroidViewModel(application) {

    private val familyRepository = FamilyRepository()

    private val _familyFlow = MutableStateFlow<Family?>(null)
    val familyFlow: StateFlow<Family?> = _familyFlow.asStateFlow()

    private val _documentIdFlow = MutableStateFlow<String?>(null)
    val documentIdFlow: StateFlow<String?> = _documentIdFlow.asStateFlow()

    private val _leaveFamilySuccess = MutableStateFlow(false)
    val leaveFamilySuccess: StateFlow<Boolean> = _leaveFamilySuccess.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchCurrentUserFamily() {
        viewModelScope.launch {
            _isLoading.value = true
            val (family, documentId) = familyRepository.fetchCurrentUserFamily()
            _familyFlow.value = family
            _documentIdFlow.value = documentId
            _isLoading.value = false
        }
    }

    fun leaveFamily() {
        viewModelScope.launch {
            _isLoading.value = true
            val success = familyRepository.leaveFamily()
            _leaveFamilySuccess.value = success
            _isLoading.value = false
        }
    }
}
