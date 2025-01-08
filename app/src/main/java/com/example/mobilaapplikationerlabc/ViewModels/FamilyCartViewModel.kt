package com.example.mobilaapplikationerlabc.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FamilyCartViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _shoppingListFlow = MutableStateFlow<List<String>>(emptyList())
    val shoppingListFlow: StateFlow<List<String>> get() = _shoppingListFlow

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchShoppingList() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid == null) {
            Log.e("FamilyCartViewModel", "No user logged in.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            db.collection("families")
                .whereArrayContains("members", currentUserUid)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val familyDoc = documents.documents[0]
                        val shoppingList = familyDoc.get("shoppingList") as? List<String> ?: emptyList()
                        _shoppingListFlow.value = shoppingList
                    } else {
                        Log.e("FamilyCartViewModel", "No family found for current user.")
                    }
                    _isLoading.value = false
                }
                .addOnFailureListener { e ->
                    _isLoading.value = false
                    Log.e("FamilyCartViewModel", "Error fetching shopping list", e)
                }
        }
    }

    fun removeItem(item: String) {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid == null) {
            Log.e("FamilyCartViewModel", "No user logged in.")
            return
        }

        viewModelScope.launch {
            db.collection("families")
                .whereArrayContains("members", currentUserUid)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val familyDoc = documents.documents[0]
                        val familyId = familyDoc.id
                        val currentList = familyDoc.get("shoppingList") as? MutableList<String> ?: mutableListOf()

                        if (currentList.contains(item)) {
                            currentList.remove(item)
                            db.collection("families")
                                .document(familyId)
                                .update("shoppingList", currentList)
                                .addOnSuccessListener {
                                    Log.d("FamilyCartViewModel", "Item removed successfully.")
                                    _shoppingListFlow.value = currentList
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FamilyCartViewModel", "Error removing item", e)
                                }
                        }
                    } else {
                        Log.e("FamilyCartViewModel", "No family found for current user.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FamilyCartViewModel", "Error fetching family document", e)
                }
        }
    }

    fun addItem(newItem: String) {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid == null) {
            Log.e("FamilyCartViewModel", "No user logged in.")
            return
        }

        viewModelScope.launch {
            db.collection("families")
                .whereArrayContains("members", currentUserUid)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val familyDoc = documents.documents[0]
                        val familyId = familyDoc.id
                        val currentList = familyDoc.get("shoppingList") as? MutableList<String> ?: mutableListOf()

                        currentList.add(newItem)
                        db.collection("families")
                            .document(familyId)
                            .update("shoppingList", currentList)
                            .addOnSuccessListener {
                                Log.d("FamilyCartViewModel", "Item added successfully.")
                                _shoppingListFlow.value = currentList
                            }
                            .addOnFailureListener { e ->
                                Log.e("FamilyCartViewModel", "Error adding item", e)
                            }
                    } else {
                        Log.e("FamilyCartViewModel", "No family found for current user.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FamilyCartViewModel", "Error fetching family document", e)
                }
        }
    }

}
