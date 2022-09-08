package ru.netology.nerecipe.obj

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeData(
    val id: Long = DEFAULT_RECIPE_ID,
    val ordinal: Long = 0L,
    val title: String = "",
    val author: String = "",
    val cuisine: String = "",
    val estimateTime: Int = TIME_NOT_SHOWN,
    val isFavorite: Boolean = false,
    val pictureSrc: String = "",
    val ingredients: String = ""
) : Parcelable {
    companion object {
        const val DEFAULT_RECIPE_ID = 0L
        const val DRAFT_ID = -1L
        const val SANDWICH_ID = -2L
        const val TIME_NOT_SHOWN = -1
    }
}
