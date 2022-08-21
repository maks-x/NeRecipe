package ru.netology.nerecipe.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nerecipe.db.*
import ru.netology.nerecipe.obj.CookingStage
import ru.netology.nerecipe.obj.RecipeData
import ru.netology.nerecipe.utils.insertSampleRecipes

class Repository(
    private val dao: RecipeDao,
    contextForSample: Context
) : RecipesRepository {

    override val recipesWithoutStages: LiveData<List<RecipeData>>
        get() {
            return dao.getAllRecipes().map { listRecipesData ->
                listRecipesData.map { it.toRecipeData() }
            }
        }

    override fun getRecipeStages(recipeId: Long) =
        dao.getRecipeStages(recipeId).map { it.toCookingStage() }

    init {
        insertSampleRecipes(contextForSample, dao) { }
    }

    override fun save(recipeData: RecipeData, stages: List<CookingStage>) {
        val stagesEntities = stages.map { it.toCookingStageEntity() }
        val isNew = recipeData.id == RecipeData.DEFAULT_RECIPE_ID
        val recipeDataEntity = recipeData.buildRecipeDataEntity()
        if (isNew) {
            dao.addRecipe(recipeDataEntity, stagesEntities)
        } else {
            dao.updateRecipeEntirely(recipeDataEntity, stagesEntities)
        }
    }

    override fun remove(recipeId: Long) {
        dao.removeRecipe(recipeId)
    }

    override fun addToFavorites(recipeId: Long) {
        dao.addToFavorites(recipeId)
    }

    override fun replaceRecipe(from: Long, to: Long) {
        //TODO репозиция элементов БД
    }


}