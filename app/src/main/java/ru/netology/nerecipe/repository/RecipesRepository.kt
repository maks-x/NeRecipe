package ru.netology.nerecipe.repository

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.obj.Recipe

interface RecipesRepository {
    val data: LiveData<List<Recipe>>
    fun save(recipe: Recipe)
    fun remove(recipeId: Long)
    fun addToFavorites(recipeId: Long)
    fun replaceRecipe(from: Int, to: Int): List<Recipe>
    fun newData(data: List<Recipe>)
}