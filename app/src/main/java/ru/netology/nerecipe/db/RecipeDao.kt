package ru.netology.nerecipe.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import ru.netology.nerecipe.obj.RecipeData

@Dao
interface RecipeDao {

    @Query("SELECT COUNT(*) FROM recipes WHERE id > 0")
    fun getCount(): Long

    @Query("UPDATE recipes SET ordinal = ordinal - 1 WHERE ordinal > :deletedOrdinal")
    fun rearrangeAfterRemove(deletedOrdinal: Long)

    @Query("SELECT * FROM recipes WHERE id > 0 ORDER BY ordinal DESC")
    fun getAllRecipes(): LiveData<List<RecipeDataEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite ORDER BY ordinal DESC")
    fun getFavorites(): LiveData<List<RecipeDataEntity>>

    @Query("SELECT * FROM recipes WHERE id == :recipeId")
    fun getRecipeById(recipeId: Long): RecipeDataEntity

    @Query("SELECT * FROM cookingStages WHERE recipeId = :recipeId ORDER BY turn ASC")
    fun getRecipeStagesEntities(recipeId: Long): List<CookingStageEntity>

    @Query("SELECT * FROM recipes WHERE id == ${RecipeData.DRAFT_ID}")
    fun getDraftRecipeData(): RecipeDataEntity?


    // region ADD

    @Insert(onConflict = REPLACE)
    fun addRecipeData(recipe: RecipeDataEntity): Long

    @Insert(onConflict = REPLACE)
    fun addCookingStage(cookingStage: CookingStageEntity)

    @Transaction
    fun addRecipe(recipeData: RecipeDataEntity, cookingStages: List<CookingStageEntity>) {
        val newOrdinal = getCount() + 1
        val newRecipeData =
            if (recipeData.id >= 0) {
                recipeData.copy(ordinal = newOrdinal)
            } else recipeData
        addRecipeData(newRecipeData).let { newRecipeId ->
            cookingStages.forEach {
                addCookingStage(it.copy(recipeId = newRecipeId))
            }
        }
    }


// endregion ADD

//region REMOVE

@Query("DELETE FROM recipes WHERE id = :recipeId")
fun deleteRecipe(recipeId: Long)

@Transaction
fun removeRecipe(recipeId: Long) {
    deleteRecipe(recipeId)
    rearrangeAfterRemove(recipeId)
}

//endregion REMOVE

// region UPDATE
@Query(
    """
            UPDATE recipes SET
            ordinal = CASE 
            WHEN ordinal = :from THEN :to 
            WHEN :from < :to THEN ordinal - 1 ELSE ordinal + 1 END
            WHERE id > 0 AND CASE WHEN :from < :to 
            THEN ordinal > :from AND ordinal <= :to OR ordinal = :from
            ELSE ordinal < :from AND ordinal >= :to OR ordinal = :from END
        """
)
fun replaceRecipe(from: Long, to: Long)

//    @Query(
//        """
//            UPDATE recipes SET
//            ordinal = :to
//            WHERE ordinal = :from
//        """
//    )
//    fun replaceRecipe(from: Long, to: Long)

//    @Query(
//        """
//            UPDATE recipes SET
//            ordinal = ordinal + CASE WHEN :from < :to THEN -1 ELSE 1 END
//            WHERE CASE WHEN :from < :to
//            THEN ordinal > :from AND ordinal < :to
//            ELSE ordinal < :from AND ordinal > :to END
//        """
//    )
//    fun rearrangeAfterReplace(from: Long, to: Long)

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