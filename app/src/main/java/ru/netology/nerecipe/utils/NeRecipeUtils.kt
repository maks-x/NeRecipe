package ru.netology.nerecipe.utils

import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.RecipeCardBinding
import ru.netology.nerecipe.databinding.RecipeStageBinding
import ru.netology.nerecipe.obj.CookingStage
import ru.netology.nerecipe.obj.Recipe

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

internal fun RecipeCardBinding.renderRecipe(recipe: Recipe) {
    val context = this.root.context
    titleDrawable.setImageResource(recipe.pictureSrc)
    author.text = context.getString(R.string.author).format(recipe.author)
    ingredients.text = recipe.ingredients.toText()
    time.text = context.getString(R.string.minutes).format(recipe.estimateTime)
    title.text = recipe.title + " ${recipe.id}"
    cuisineCategory.text = context.getString(R.string.cuisine).format(recipe.cuisine)
}

internal fun RecipeStageBinding.renderStage(stage: CookingStage) {
    val context = this.root.context
    stage.illustrationSrc?.let { titleDrawable.setImageResource(it) }
    guidance.text = stage.guidance
    turn.text = "${stage.turn}"
}