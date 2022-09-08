package ru.netology.nerecipe.repository

import android.content.Context
import androidx.lifecycle.map
import ru.netology.nerecipe.db.*
import ru.netology.nerecipe.obj.CookingStage
import ru.netology.nerecipe.obj.RecipeData
import ru.netology.nerecipe.utils.insertSampleRecipes

class Repository(
    private val dao: RecipeDao,
    contextForSample: Context
) : RecipesRepository {

    init {
        insertSampleRecipes(contextForSample, dao)
    }

    override var recipeDraftPair: Pair<RecipeData, List<CookingStage>>?
        get() = dao.getDraftRecipeData()?.let { recipeDataEntity ->
            Pair(
                recipeDataEntity.toRecipeData(),
                dao.getRecipeStagesEntities(RecipeData.DRAFT_ID).map { it.toCookingStage() }
            )
        }
        set(value) {
            value?.let { recipePair ->
                dao.addRecipe(
                    recipePair.first.toRecipeDataEntity(),
                    recipePair.second.map { it.toCookingStageEntity() }
                )
            } ?: dao.removeRecipe(RecipeData.DRAFT_ID)
        }

    override val recipesWithoutStages = dao.getAllRecipes().map { listRecipesData ->
        listRecipesData.map { it.toRecipeData() }
    }

    override fun getRecipeData(recipeId: Long) =
        getRecipeDataEntity(recipeId).toRecipeData()

    private fun getRecipeDataEntity(recipeId: Long) =
        dao.getRecipeById(recipeId)

    override fun getRecipeStages(recipeId: Long) =
        getStagesEntities(recipeId).map { it.toCookingStage() }

    private fun getStagesEntities(recipeId: Long) =
        dao.getRecipeStagesEntities(recipeId)

    override fun save(recipeData: RecipeData, stages: List<CookingStage>) {
        val isNew = recipeData.id == RecipeData.DEFAULT_RECIPE_ID
        val stagesEntities = stages.map { it.toCookingStageEntity() }
        val recipeDataEntity = recipeData.toRecipeDataEntity()
        if (isNew) {
            dao.addRecipe(recipeDataEntity, stagesEntities)
        } else {
            val isFavorite = getRecipeDataEntity(recipeData.id).isFavorite
            dao.updateRecipe(recipeDataEntity.copy(isFavorite = isFavorite))
            val currentStagesIds = getStagesEntities(recipeData.id).map { it.id }
            stagesEntities.forEachIndexed { index, cookingStageEntity ->
                if (index <= currentStagesIds.lastIndex) {
                    dao.updateStage(cookingStageEntity.copy(id = currentStagesIds[index]))
                } else dao.addCookingStage(cookingStageEntity)
            }
            if (stages.size < currentStagesIds.size)
                dao.removeExtraStages(recipeData.id, stages.size)
        }
    }

    override fun remove(recipeId: Long) {
        dao.removeRecipe(recipeId)
    }

    override fun addToFavorites(recipeId: Long) {
        dao.addToFavorites(recipeId)
    }

    override fun replaceRecipe(from: Long, to: Long) {
        dao.replaceRecipe(from, to)
//        dao.rearrangeAfterReplace(from, to)
    }


}