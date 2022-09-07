package ru.netology.nerecipe.obj

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CookingStage(
    val id: Long = DEFAULT_STAGE_ID,
    val recipeId: Long = RecipeData.DEFAULT_RECIPE_ID,
    val turn: Int = 1,
    val guidance: String = "",
    val illustrationSrc: String? = null
) : Parcelable {
    companion object {
        const val DEFAULT_STAGE_ID = 0L
    }
}
