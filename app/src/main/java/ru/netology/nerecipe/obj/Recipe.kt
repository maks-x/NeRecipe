package ru.netology.nerecipe.obj

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Long = DEFAULT_RECIPE_ID,
    val title: String,
    val author: String,
    val cuisine: String,
    val estimateTime: Int,
    val favorite: Boolean,
    val pictureSrc: Int,
    val ingredients: List<String>,
    val stages: List<CookingStage>
): Parcelable {
    companion object {
        const val DEFAULT_RECIPE_ID = 0L
    }
}
