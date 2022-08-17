package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.obj.Recipe
import ru.netology.nerecipe.repository.Repository
import ru.netology.nerecipe.utils.SingleEvent

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipeInteractionListener {

    private val repository = Repository(application)

    val data by repository::data

    val recipeClickedEvent = MutableLiveData<SingleEvent<Recipe>>()

    private val _linkImgData = MutableLiveData<String>()
    val linkImgData: LiveData<String>
        get() = _linkImgData
    fun setLinkValue(string: String) {
        _linkImgData.value = string
    }


    // region RecipeInteractionListener

    override fun onReplaceRecipeView(from: Int, to: Int): List<Recipe> {
        return repository.replaceRecipe(from, to)
    }

    override fun newData(data: List<Recipe>) {
        repository.newData(data)
    }
    override fun onRemoveClick(recipeId: Long) {
        repository.remove(recipeId)
    }

    override fun onEditClick(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override fun onAddToFavoritesClick(recipeId: Long) {
        repository.addToFavorites(recipeId)
    }

    override fun onRecipeClick(recipe: Recipe) {
        recipeClickedEvent.value = SingleEvent(recipe)
    }
    // endregion RecipeInteractionListener
}