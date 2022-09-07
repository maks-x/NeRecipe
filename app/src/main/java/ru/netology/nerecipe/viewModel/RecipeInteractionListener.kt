package ru.netology.nerecipe.viewModel

import ru.netology.nerecipe.obj.CookingStage
import ru.netology.nerecipe.obj.RecipeData

interface RecipeInteractionListener {
    fun saveRecipe(recipeData: RecipeData, stages: List<CookingStage>)
    fun onReplaceRecipeCard(from: Int, to: Int)
    fun onRemoveClick(recipeId: Long)
    fun onEditClick(recipeId: Long, fromFragmentTag: String)
    fun onAddToFavoritesClick(recipeId: Long)
    fun onRecipeClick(recipeId: Long, fromFragmentTag: String)
}