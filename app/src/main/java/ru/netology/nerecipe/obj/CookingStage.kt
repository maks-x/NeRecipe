package ru.netology.nerecipe.obj

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CookingStage(
    val recipeId: Long,
    val turn: Int,
    val guidance: String,
    val illustrationSrc: Int?
) : Parcelable
