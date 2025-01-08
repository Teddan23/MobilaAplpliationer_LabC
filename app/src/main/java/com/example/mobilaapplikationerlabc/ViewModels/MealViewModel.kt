import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilaapplikationerlabc.MealToFirebase.RecipeRepository
import com.example.mobilaapplikationerlabc.model.Meal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealViewModel : ViewModel() {

    private val recipeRepository = RecipeRepository()

    private val _isMealFavorite = MutableStateFlow(false)
    val isMealFavorite: StateFlow<Boolean> = _isMealFavorite

    // Metod för att spara måltider från API till Firestore
    fun saveMeal(meal: Meal) {
            // När du har fått svaret, spara alla måltider
            recipeRepository.saveMeal(meal)
    }

    fun removeMeal(mealId: String){
        recipeRepository.removeMealFromFamily(mealId)
    }

    fun checkIfMealIsInFamily(mealId: String) {
        viewModelScope.launch {
            recipeRepository.isMealInFamily(mealId) { isInFamily ->
                _isMealFavorite.value = isInFamily
            }
        }
    }

    fun removeFromFavorites(meal: Meal) {
        recipeRepository.removeMealFromFamily(meal.idMeal)
    }

    fun checkIfMealIsInFamily(mealId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            recipeRepository.isMealInFamily(mealId) { isInFamily ->
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