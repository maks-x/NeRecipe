package ru.netology.nerecipe.obj

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeData(
    val id: Long = DEFAULT_RECIPE_ID,
    val title: String,
    val author: String,
    val cuisine: String,
    val estimateTime: Int,
    val isFavorite: Boolean,
    val pictureSrc: String,
    val ingredients: String
): Parcelable {
    companion object {
        const val DEFAULT_RECIPE_ID = 0L
        const val DRAFT_ORDINAL_NUMBER = -1L
    }
}
