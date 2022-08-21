package ru.netology.nerecipe.viewModel

import ru.netology.nerecipe.obj.RecipeData

interface RecipeInteractionListener {
    fun onReplaceRecipeCard(from: Int, to: Int)
    fun onRemoveClick(recipeId: Long)
    fun onEditClick(recipeData: RecipeData)
    fun onAddToFavoritesClick(recipeId: Long)
    fun onRecipeClick(recipeData: RecipeData)
}