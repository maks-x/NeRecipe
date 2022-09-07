package ru.netology.nerecipe.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.netology.nerecipe.R
import ru.netology.nerecipe.obj.RecipeData
import java.util.concurrent.Executors

@Database(
    entities = [RecipeDataEntity::class, CookingStageEntity::class],
    version = 1
)
abstract class AppDb : RoomDatabase() {
    abstract val recipeDao: RecipeDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db").addCallback(
                object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Executors.newSingleThreadExecutor().execute {
                            getInstance(context).recipeDao.apply {
                                addRecipe(
                                    recipeData = RecipeDataEntity(
                                        id = RecipeData.SANDWICH_ID,
                                        author = context.getString(R.string.buter_author),
                                        estimateTime = 1,
                                        title = context.getString(R.string.buter_title),
                                        pictureSrc = R.drawable.buter.toString(),
                                        isFavorite = true,
                                        cuisine = context.getString(R.string.russian_category),
                                        ingredients = "хлеб, колбаса"
                                    ),
                                    cookingStages = listOf(
                                        CookingStageEntity(
                                            recipeId = RecipeData.SANDWICH_ID,
                                            turn = 1,
                                            guidance = "Отрежь ломоть хлеба 1 см толщиной",
                                            illustrationSrc = R.drawable.bread.toString()
                                        ),
                                        CookingStageEntity(
                                            recipeId = RecipeData.SANDWICH_ID,
                                            turn = 2,
                                            guidance = "Отрежь два ломтя колбасы 0,5 см толщиной",
                                            illustrationSrc = R.drawable.sausage.toString()
                                        ),
                                        CookingStageEntity(
                                            recipeId = RecipeData.SANDWICH_ID,
                                            turn = 3,
                                            guidance = "Складывваем (как Дядя Фёдор или Кот Матроскин): готово! Приятного аппетита!",
                                            illustrationSrc = R.drawable.buter.toString()
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            ).allowMainThreadQueries().build()
    }
}