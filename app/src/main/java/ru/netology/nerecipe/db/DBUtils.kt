package ru.netology.nerecipe.db

import ru.netology.nerecipe.obj.CookingStage
import ru.netology.nerecipe.obj.RecipeData

internal fun RecipeData.toRecipeDataEntity() = RecipeDataEntity(
    id = id,
    title = title,
    author = author,
    cuisine = cuisine,
    estimateTime = estimateTime,
    isFavorite = isFavorite,
    pictureSrc = pictureSrc,
    ingredients = ingredients
)

internal fun CookingStage.toCookingStageEntity() = CookingStageEntity(
    id = id,
    recipeId = recipeId,
    turn = turn,
    guidance = guidance,
    illustrationSrc = illustrationSrc
)

internal fun CookingStageEntity.toCookingStage() = CookingStage(
    id = id,
    recipeId = recipeId,
    turn = turn,
    guidance = guidance,
    illustrationSrc = illustrationSrc
)

internal fun RecipeDataEntity.toRecipeData() = RecipeData(
    id = id,
    title = title,
    author = author,
    cuisine = cuisine,
    estimateTime = estimateTime,
    isFavorite = isFavorite,
    pictureSrc = pictureSrc,
    ingredients = ingredients
)