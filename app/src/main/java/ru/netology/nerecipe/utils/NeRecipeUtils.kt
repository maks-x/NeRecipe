package ru.netology.nerecipe.utils

import android.content.Context
import androidx.core.content.edit
import coil.load
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.RecipeCardBinding
import ru.netology.nerecipe.databinding.RecipeStageBinding
import ru.netology.nerecipe.db.RecipeDao
import ru.netology.nerecipe.db.buildRecipeDataEntity
import ru.netology.nerecipe.db.toCookingStageEntity
import ru.netology.nerecipe.obj.CookingStage
import ru.netology.nerecipe.obj.RecipeData

@Override
internal fun List<String>.toText(): String {
    val sb = StringBuilder()
    forEach { sb.append(it).append(", ") }
    return sb.toString()
        .substringBeforeLast(',')
        .let { result ->
            result.replaceFirstChar { it.uppercaseChar() }
        }
}

internal fun RecipeCardBinding.renderRecipeCard(recipeData: RecipeData) {
    val context = this.root.context
    titleDrawable.load(recipeData.pictureSrc)
    author.text = context.getString(R.string.author).format(recipeData.author)
    ingredients.text = recipeData.ingredients
    time.text = context.getString(R.string.minutes).format(recipeData.estimateTime)
    title.text = recipeData.title
    cuisineCategory.text = context.getString(R.string.cuisine).format(recipeData.cuisine)
    favorites.isChecked = recipeData.isFavorite
}

internal fun RecipeStageBinding.renderStage(stage: CookingStage) {
    val context = this.root.context
    stage.illustrationSrc?.let { titleDrawable.load(it) }
    guidance.text = stage.guidance
    turn.text = "${stage.turn}"
}

internal fun insertSampleRecipes(context: Context, dao: RecipeDao, callback: (Int) -> Unit) {

    val prefs = context.getSharedPreferences(COMMON_SHARED_PREFS_KEY, Context.MODE_PRIVATE)

    if (!prefs.getBoolean(FIRST_START_PREFS_KEY, true))
        return

    prefs.edit {
        putBoolean(FIRST_START_PREFS_KEY, false)
    }

    val startCount = 10

    List(startCount) { index ->
        RecipeData(
            id = index.toLong() + 1,
            author = context.getString(R.string.buter_author),
            estimateTime = 1,
            title = context.getString(R.string.buter_title) + index.plus(1),
            pictureSrc = R.drawable.buter.toString(),
            isFavorite = true,
            cuisine = context.getString(R.string.russian_category),
            ingredients = "хлеб, колбаса"
        )
    }.forEachIndexed { index, recipeData ->
        val recipeDataEntity = recipeData.copy(id = index.toLong() + 1).buildRecipeDataEntity()
        val stages = listOf(
            CookingStage(
                recipeId = recipeDataEntity.id,
                turn = 1,
                guidance = "Отрежь ломоть хлеба 1 см толщиной",
                illustrationSrc = R.drawable.bread.toString()
            ),
            CookingStage(
                recipeId = recipeDataEntity.id,
                turn = 2,
                guidance = "Отрежь два ломтя колбасы 0,5 см толщиной",
                illustrationSrc = R.drawable.sausage.toString()
            ),
            CookingStage(
                recipeId = recipeDataEntity.id,
                turn = 3,
                guidance = "Один ломоть на другой (как Дядя Фёдор или Кот Матроскин): готово! Приятного аппетита" +
                        "!\n".repeat(30),
                illustrationSrc = R.drawable.buter.toString()
            )
        ).map { it.toCookingStageEntity() }
        dao.addRecipe(recipeDataEntity, stages)
    }
    callback(startCount)
}

// region APP_CONSTANTS

const val COMMON_SHARED_PREFS_KEY = "commonPrefs"
const val FIRST_START_PREFS_KEY = "firstStart"


// endregion APP_CONSTANTS