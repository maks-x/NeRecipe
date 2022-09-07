package ru.netology.nerecipe.repository

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.obj.CookingStage
import ru.netology.nerecipe.obj.RecipeData

interface RecipesRepository {
    var recipeDraftPair: Pair<RecipeData, List<CookingStage>>?
    val recipesWithoutStages: LiveData<List<RecipeData>>
    fun getRecipeData(recipeId: Long): RecipeData
    fun getRecipeStages(recipeId: Long): List<CookingStage>
    fun save(recipeData: RecipeData, stages: List<CookingStage>)
    fun remove(recipeId: Long)
    fun addToFavorites(recipeId: Long)
    fun replaceRecipe(from: Long, to: Long)
}