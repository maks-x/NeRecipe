package ru.netology.nerecipe.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "cookingStages",
    foreignKeys = [
        ForeignKey(
            entity = RecipeDataEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
class CookingStageEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "recipeId")
    val recipeId: Long = 0L,

    @ColumnInfo(name = "turn")
    val turn: Int,

    @ColumnInfo(name = "guidance")
    val guidance: String,

    @ColumnInfo(name = "illustrationSrc")
    val illustrationSrc: String?
)