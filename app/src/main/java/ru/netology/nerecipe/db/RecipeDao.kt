package ru.netology.nerecipe.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): LiveData<List<RecipeDataEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite ORDER BY id DESC")
    fun getFavorites(): LiveData<List<RecipeDataEntity>>

    @Query("SELECT * FROM cookingStages WHERE recipeId = :recipeId ORDER BY turn ASC")
    fun getRecipeStages(recipeId: Long): List<CookingStageEntity>

    // region ADD

    @Insert()
    fun addRecipeData(recipe: RecipeDataEntity)

    @Insert
    fun addCookingStage(cookingStage: CookingStageEntity)

    @Transaction
    fun addRecipe(recipeData: RecipeDataEntity, cookingStages: List<CookingStageEntity>) {
        addRecipeData(recipeData)
        cookingStages.forEach {
            addCookingStage(it)
        }
    }

    // endregion ADD

    //region REMOVE

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    fun removeRecipe(recipeId: Long)

    //endregion REMOVE

    // region UPDATE

    @Update
    fun updateRecipe(recipe: RecipeDataEntity)

    @Update
    fun updateStage(stage: CookingStageEntity)

    @Query("DELETE FROM cookingStages WHERE recipeId = :recipeId AND turn > :currentStagesCount")
    fun removeExtraStages(recipeId: Long, currentStagesCount: Int)

    @Transaction
    fun updateRecipeEntirely(recipeData: RecipeDataEntity, stages: List<CookingStageEntity>) {
        stages.forEach {
            updateStage(it)
        }
        removeExtraStages(recipeData.id, stages.size)
        updateRecipe(recipeData)
    }

    @Query(
        """
            UPDATE recipes SET
            isFavorite = CASE WHEN isFavorite THEN 0 ELSE 1 END
            WHERE id = :id
        """
    )
    fun addToFavorites(id: Long)


    // endregion UPDATE



}