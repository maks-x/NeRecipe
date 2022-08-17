package ru.netology.nerecipe.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.R
import ru.netology.nerecipe.obj.CookingStage
import ru.netology.nerecipe.obj.Recipe
import java.util.*

class Repository(
    application: Application
) : RecipesRepository {

    private val _data = MutableLiveData<List<Recipe>>(emptyList())

    override val data: LiveData<List<Recipe>>
        get() = _data

    init {
        val buterStages = listOf(
            CookingStage(
                0, 1, "Отрежь ломоть хлеба 1 см толщиной", R.drawable.bread
            ),
            CookingStage(
                0, 2, "Отрежь ломоть колбасы 0,5 см толщиной", R.drawable.sausage
            ),
            CookingStage(
                0, 3,
                "Один ломоть на другой (как Дядя Фёдор или Кот Матроскин): готово! Приятного аппетита!\n!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!\n" +
                        "!",
                R.drawable.buter
            )
        )
        _data.value = List(100) { index ->
            Recipe(
                id = index.toLong(),
                author = application.getString(R.string.buter_author),
                estimateTime = 1,
                title = application.getString(R.string.buter_title),
                pictureSrc = R.drawable.buter,
                favorite = true,
                cuisine = application.getString(R.string.russian_category),
                ingredients = listOf("хлеб", "колбаса"),
                stages = buterStages
            )
        }
    }

    override fun save(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override fun remove(recipeId: Long) {
        TODO("Not yet implemented")
    }

    override fun addToFavorites(recipeId: Long) {
        TODO("Not yet implemented")
    }
    override fun newData(data: List<Recipe>) {
        _data.value = data
    }

    override fun replaceRecipe(from: Int, to: Int): List<Recipe> {
        val list = checkNotNull(_data.value).toMutableList()
        val fromRecipe = list[from]
        list.removeAt(from)
        if (from > to) {
            list.add(to, fromRecipe)
        } else {
            list.add(to, fromRecipe)
        }
        val result = list.toList()
        _data.value = result
        return result
    }


}