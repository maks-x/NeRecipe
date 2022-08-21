package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import ru.netology.nerecipe.R
import ru.netology.nerecipe.db.AppDb
import ru.netology.nerecipe.obj.CookingStage
import ru.netology.nerecipe.obj.RecipeData
import ru.netology.nerecipe.repository.RecipesRepository
import ru.netology.nerecipe.repository.Repository
import ru.netology.nerecipe.utils.SingleEvent

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipeInteractionListener {

    private val repository: RecipesRepository = Repository(
        dao = AppDb.getInstance(
            context = application
        ).recipeDao,
        contextForSample = application
    )

    private val currentCuisineFilters =
        application.resources.getStringArray(R.array.cuisine_categories).toMutableList()

    fun showCuisine(cuisine: String, callback :(List<RecipeData>) -> Unit) {
        if (currentCuisineFilters.contains(cuisine)) return
        currentCuisineFilters.add(cuisine)
        callback(recipes)
    }

    fun hideCuisine(cuisine: String, callback :(List<RecipeData>) -> Unit) {
        currentCuisineFilters.remove(cuisine)
        callback(recipes)
    }

    val data by repository::recipesWithoutStages
    private val recipes
        get() = checkNotNull(data.value) {
            "EMPTY DATA!!!"
        }.filter{
            currentCuisineFilters.contains(it.cuisine)
        }

    val filteredEvent = MutableLiveData<SingleEvent<List<RecipeData>>>()

    val recipeClickedEvent = MutableLiveData<SingleEvent<RecipeData>>()

    private val _linkImgData = MutableLiveData<String>()
    val linkImgData: LiveData<String>
        get() = _linkImgData

    fun setLinkValue(string: String) {
        _linkImgData.value = string
    }

    fun dataFilteredBy(predicate: (RecipeData) -> Boolean) {
        val filteredRecipes = recipes.filter(predicate)
        filteredEvent.value = SingleEvent(filteredRecipes)
    }

    fun dataFilteredBy(vararg predicates: (RecipeData) -> Boolean): List<RecipeData> {
        val list = recipes.toMutableList()
        predicates.forEach { predicate ->
            list.retainAll(predicate)
        }
        return list
    }

    val currentStages = MutableLiveData<SingleEvent<List<CookingStage>>>()
    fun stagesQuery(recipeId: Long){
        currentStages.value = SingleEvent(repository.getRecipeStages(recipeId))
    }

    // region RecipeInteractionListener

    // как быть, если элементов станет больше, чем Integer.MAX_VALUE?
    // ведь viewHolder.absoluteAdapterPosition возвращает Int
    override fun onReplaceRecipeCard(from: Int, to: Int) {
        repository.replaceRecipe(from.toLong(), to.toLong())
    }

    override fun onRemoveClick(recipeId: Long) {
        repository.remove(recipeId)
    }

    override fun onEditClick(recipeData: RecipeData) {
        TODO("Not yet implemented")
    }

    override fun onAddToFavoritesClick(recipeId: Long) {
        repository.addToFavorites(recipeId)
    }

    override fun onRecipeClick(recipeData: RecipeData) {

        recipeClickedEvent.value = SingleEvent(recipeData)
    }
    // endregion RecipeInteractionListener
}