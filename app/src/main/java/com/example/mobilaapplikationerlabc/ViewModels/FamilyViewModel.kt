package com.example.mobilaapplikationerlabc.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.mobilaapplikationerlabc.DataClasses.Family
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FamilyViewModel(application: Application) : AndroidViewModel(application) {

    private val _familyFlow = MutableStateFlow<Family?>(null)
    val familyFlow: StateFlow<Family?> = _familyFlow.asStateFlow()

    private val _leaveFamilySuccess = MutableStateFlow(false) // Indikator för om användaren har lämnat familjen
    val leaveFamilySuccess: StateFlow<Boolean> = _leaveFamilySuccess.asStateFlow()

    private val auth = FirebaseAuth.getInstance() // Definieras här som instansvariabel
    private val db = FirebaseFirestore.getInstance() // Definieras här som instansvariabel

    fun fetchCurrentUserFamily() {
        val currentUserId = auth.currentUser?.uid ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection("families")
            .whereArrayContains("members", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Hämta den första familjen som matchar användarens UID
                    val family = querySnapshot.documents[0].toObject(Family::class.java)
                    family?.let {
                        // Uppdatera flödet med den hittade familjen
                        _familyFlow.value = it
                    }
                }
            }
            .addOnFailureListener {
                _familyFlow.value = null // Om hämtningen misslyckas
            }
    }

    fun leaveFamily() {
        val currentUserId = auth.currentUser?.uid ?: return
        val family = _familyFlow.value // Hämta aktuell familj från _familyFlow

        family?.let {
            // Hämta det första dokumentet från Firestore, som vi använde i fetchCurrentUserFamily()
            db.collection("families")
                .whereArrayContains("members", currentUserId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val familyDocument = querySnapshot.documents[0] // Hämtar första dokumentet
                        val familyId = familyDocument.id // Hämta dokumentets ID direkt här
                        val updatedMembers = it.members.toMutableList()

                        if (it.members.size == 1) {
                            // Ta bort hela familjen från Firestore
                            db.collection("families")
                                .document(familyId)
                                .delete()
                                .addOnSuccessListener {
                                    _leaveFamilySuccess.value = true
                                    Log.d("FamilyViewModel", "Family deleted as the last member left.")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FamilyViewModel", "Error deleting family", e)
                                }
                        }
                        else{
                            // Ta bort användarens UID från "members" listan
                            updatedMembers.remove(currentUserId)

                            // Uppdatera familjens "members" lista i Firestore
                            db.collection("families")
                                .document(familyId) // Använd dokumentets ID för att identifiera familjen
                                .update("members", updatedMembers)
                                .addOnSuccessListener {

                                    _leaveFamilySuccess.value = true
                                }
                                .addOnFailureListener { e ->
                                    // Hantera eventuella fel vid uppdatering
                                    Log.e("FamilyViewModel", "Error leaving family", e)
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FamilyViewModel", "Error fetching family document", e)
                }
        }
    }

}
