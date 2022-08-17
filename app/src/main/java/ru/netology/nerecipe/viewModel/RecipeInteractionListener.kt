package ru.netology.nerecipe.viewModel

import ru.netology.nerecipe.obj.Recipe

interface RecipeInteractionListener {
    fun onReplaceRecipeView(from: Int, to: Int): List<Recipe>
    fun onRemoveClick(recipeId: Long)
    fun onEditClick(recipe: Recipe)
    fun onAddToFavoritesClick(recipeId: Long)
    fun onRecipeClick(recipe: Recipe)
    fun newData(data: List<Recipe>)
}