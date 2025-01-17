import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilaapplikationerlabc.Repositories.MealRepository
import com.example.mobilaapplikationerlabc.model.Meal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealViewModel : ViewModel() {

    private val mealRepository = MealRepository()

    private val _isMealFavorite = MutableStateFlow(false)
    val isMealFavorite: StateFlow<Boolean> = _isMealFavorite

    fun saveMeal(meal: Meal) {
            mealRepository.saveMeal(meal)
    }

    fun removeMeal(mealId: String){
        mealRepository.removeMealFromFamily(mealId)
    }

    fun checkIfMealIsInFamily(mealId: String) {
        viewModelScope.launch {
            mealRepository.isMealInFamily(mealId) { isInFamily ->
                _isMealFavorite.value = isInFamily
            }
        }
    }

    fun removeFromFavorites(meal: Meal) {
        mealRepository.removeMealFromFamily(meal.idMeal)
    }

    fun checkIfMealIsInFamily(mealId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            mealRepository.isMealInFamily(mealId) { isInFamily ->
                callback(isInFamily)
            }
        }
    }









    /*
    // En lista över favoriter (simulerad; kan kopplas till Room för persistens)
    private val _favoriteMeals = MutableStateFlow<Set<String>>(emptySet())
    val favoriteMeals: StateFlow<Set<String>> = _favoriteMeals

    // Kontrollera om en måltid är favorit
    fun isMealFavorite(mealId: String): StateFlow<Boolean> {
        val isFavoriteFlow = MutableStateFlow(false)
        viewModelScope.launch {
            _favoriteMeals.collect { favorites ->
                isFavoriteFlow.value = mealId in favorites
            }
        }
        return isFavoriteFlow
    }

    // Lägg till en måltid i favoriter
    fun addToFavorites(meal: Meal) {
        viewModelScope.launch {
            _favoriteMeals.value = _favoriteMeals.value + meal.idMeal
        }
    }

    // Ta bort en måltid från favoriter
    fun removeFromFavorites(meal: Meal) {
        viewModelScope.launch {
            _favoriteMeals.value = _favoriteMeals.value - meal.idMeal
        }
    }*/


}