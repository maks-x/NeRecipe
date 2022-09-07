package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nerecipe.R
import ru.netology.nerecipe.db.AppDb
import ru.netology.nerecipe.obj.CookingStage
import ru.netology.nerecipe.obj.RecipeData
import ru.netology.nerecipe.repository.RecipesRepository
import ru.netology.nerecipe.repository.Repository
import ru.netology.nerecipe.ui.AllFragment
import ru.netology.nerecipe.ui.DetailsFragment
import ru.netology.nerecipe.ui.FavoritesFragment
import ru.netology.nerecipe.utils.SingleLiveEvent
import ru.netology.nerecipe.utils.cuisineIDtoString

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipeInteractionListener {

    private val repository: RecipesRepository = Repository(
        dao = AppDb.getInstance(
            context = application
        ).recipeDao,
        contextForSample = application.applicationContext
    )

    private val _data by repository::recipesWithoutStages
    val data
        get() = _data.map { it.filtered() }

    var recipeDraftPair by repository::recipeDraftPair
        private set

    fun setDraft(draftRecipe: Pair<RecipeData, List<CookingStage>>?) {
        recipeDraftPair = draftRecipe
    }

    fun clearDraft() {
        recipeDraftPair = null
    }

    val favoriteRecipes
        get() = _data.map { recipes ->
            recipes.filter { it.isFavorite }
        }

    private val allRecipes
        get() = checkNotNull(_data.value)

    private val _filterEvent = SingleLiveEvent<List<RecipeData>>()
    val filterEvent: LiveData<List<RecipeData>>
        get() = _filterEvent

    private val _navigateFromAllToEditEvent = SingleLiveEvent<Long>()
    val navigateToCreateFragmentEvent: LiveData<Long>
        get() = _navigateFromAllToEditEvent
    private val _navigateFromFavoritesToEditEvent = SingleLiveEvent<Long>()
    val navigateFromFavoritesToEditEvent: LiveData<Long>
        get() = _navigateFromFavoritesToEditEvent
    private val _navigateFromDetailsToEditEvent = SingleLiveEvent<Long>()
    val navigateFromDetailsToEditEvent: LiveData<Long>
        get() = _navigateFromDetailsToEditEvent


    private val allCuisines =
        application.resources.getStringArray(R.array.cuisine_categories).toList()

    private var currentCuisineFilters = allCuisines

    var currentTitleFilter = ""
        private set

    private fun List<RecipeData>.filtered() =
        filter {
            currentCuisineFilters.contains(it.cuisine)
        }.filter {
            it.title.contains(currentTitleFilter)
        }

    fun submitCuisineFilter(filtersIds: List<Int>) {
        currentCuisineFilters =
            if (filtersIds.isEmpty()) {
                allCuisines
            } else {
                filtersIds.map { cuisineChipId ->
                    getApplication<Application>().cuisineIDtoString(cuisineChipId)
                }
            }
        _filterEvent.value = allRecipes.filtered()
    }

    fun submitTitleFilter(filter: String) {
        currentTitleFilter = filter
        _filterEvent.value = allRecipes.filtered()
    }

    val filteredEvent = SingleLiveEvent<List<RecipeData>>()

    val onAllFragmentRecipeClickedEvent = SingleLiveEvent<Long>()
    val onFavoritesFragmentRecipeClickedEvent = SingleLiveEvent<Long>()

    val stagesRenderingEvent = SingleLiveEvent<List<CookingStage>>()
    fun renderStageRequest(recipeId: Long) {
        stagesRenderingEvent.value = repository.getRecipeStages(recipeId)
    }

    val recipeRenderingEvent = SingleLiveEvent<Pair<RecipeData, List<CookingStage>>?>()
    fun renderRecipeRequest(recipeId: Long) {
        recipeRenderingEvent.value =
            if (recipeId == RecipeData.DRAFT_ID_NEW) {
                repository.recipeDraftPair
            } else Pair(
                repository.getRecipeData(recipeId),
                repository.getRecipeStages(recipeId)
            )
    }

    // region RecipeInteractionListener
    override fun saveRecipe(recipeData: RecipeData, stages: List<CookingStage>) {
        repository.save(recipeData, stages)
    }

    // как быть, если элементов станет больше, чем Integer.MAX_VALUE?
    // ведь viewHolder.absoluteAdapterPosition возвращает Int
    override fun onReplaceRecipeCard(from: Int, to: Int) {
        repository.replaceRecipe(from.toLong(), to.toLong())
    }

    override fun onRemoveClick(recipeId: Long) {
        repository.remove(recipeId)
    }

    override fun onEditClick(recipeId: Long, fromFragmentTag: String) {
        when (fromFragmentTag) {
            AllFragment.FROM_ALL_FRAGMENT_TAG ->
                _navigateFromAllToEditEvent.value = recipeId
            FavoritesFragment.FROM_FAVORITES_FRAGMENT_TAG ->
                _navigateFromFavoritesToEditEvent.value = recipeId
            DetailsFragment.FROM_DETAILS_FRAGMENT_TAG ->
                _navigateFromDetailsToEditEvent.value = recipeId
        }
    }

    override fun onAddToFavoritesClick(recipeId: Long) {
        repository.addToFavorites(recipeId)
    }

    override fun onRecipeClick(recipeId: Long, fromFragmentTag: String) {
        when (fromFragmentTag) {
            AllFragment.FROM_ALL_FRAGMENT_TAG ->
                onAllFragmentRecipeClickedEvent.value = recipeId
            FavoritesFragment.FROM_FAVORITES_FRAGMENT_TAG ->
                onFavoritesFragmentRecipeClickedEvent.value = recipeId
        }

    }
    // endregion RecipeInteractionListener
}