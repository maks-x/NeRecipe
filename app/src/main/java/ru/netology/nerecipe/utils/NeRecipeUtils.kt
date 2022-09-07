package ru.netology.nerecipe.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.core.view.children
import androidx.core.view.size
import coil.load
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.FragmentAllBinding
import ru.netology.nerecipe.databinding.FragmentCreateBinding
import ru.netology.nerecipe.databinding.RecipeCardBinding
import ru.netology.nerecipe.databinding.RecipeStageBinding
import ru.netology.nerecipe.db.RecipeDao
import ru.netology.nerecipe.db.toCookingStageEntity
import ru.netology.nerecipe.db.toRecipeDataEntity
import ru.netology.nerecipe.obj.CookingStage
import ru.netology.nerecipe.obj.RecipeData
import ru.netology.nerecipe.viewModel.RecipeInteractionListener

internal fun ImageView.loadResOrURL(src: String?) {
    val source = try {
        src?.toInt()
    } catch (ex: NumberFormatException) {
        Log.d(ex.toString(), "unable to parse recipe card pictureSrc")
        src
    } ?: src
    load(source) { error(R.drawable.error_load) }
}

internal fun RecipeCardBinding.renderRecipeCard(recipeData: RecipeData) {
    val context = this.root.context
    titleDrawable.loadResOrURL(recipeData.pictureSrc)
    author.text = context.getString(R.string.author_template).format(recipeData.author)
    ingredients.text = recipeData.ingredients
    time.text = context.getString(R.string.minutes).format(recipeData.estimateTime)
    title.text = recipeData.title
    cuisineCategory.text = context.getString(R.string.cuisine_template).format(recipeData.cuisine)
    favorites.isChecked = recipeData.isFavorite
}

internal fun RecipeStageBinding.renderStage(stage: CookingStage) {
    titleDrawable.loadResOrURL(stage.illustrationSrc)
    guidance.text = stage.guidance
    turn.text = "${stage.turn}"
}

internal fun Context.cuisineIDtoString(id: Int) =
    when (id) {
        R.id.russian -> getString(R.string.russian_category)
        R.id.european -> getString(R.string.european_category)
        R.id.american -> getString(R.string.american_category)
        R.id.eastern -> getString(R.string.eastern_category)
        R.id.asian -> getString(R.string.asian_category)
        R.id.pan_asian -> getString(R.string.pan_asian_category)
        R.id.mediterranean -> getString(R.string.mediterranean_category)
        else -> "wrong CuisineID request"
    }

internal fun insertSampleRecipes(context: Context, dao: RecipeDao, callback: (Int) -> Unit = {}) {

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
            cuisine = context.getString(
                if (index % 2 > 0) R.string.russian_category else R.string.mediterranean_category
            ),
            ingredients = "хлеб, колбаса"
        )
    }.forEachIndexed { index, recipeData ->
        val recipeDataEntity = recipeData.copy(id = index.toLong() + 1).toRecipeDataEntity()
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
                        "!\n",
                illustrationSrc = R.drawable.buter.toString()
            )
        ).map { it.toCookingStageEntity() }
        dao.addRecipe(recipeDataEntity, stages)
    }
    callback(startCount)
}

internal fun View.switchVisibility(condition: Boolean = true) {
    if (condition) visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
}


internal fun FragmentCreateBinding.inflateNewStageLayout(
    action: TextInputLayout.() -> Unit = {}
): TextInputLayout {
    TextInputLayout.inflate(
        root.context, R.layout.stages_layout_sample, stagesContainer
    )
    val stageInputLayout: TextInputLayout =
        stagesContainer.children.last { it is TextInputLayout } as TextInputLayout

    val illustrationInput =
        stageInputLayout.children.last { it is TextInputLayout } as TextInputLayout

    val illustrationImageView =
        stageInputLayout.children.last { it is ImageView } as ImageView

    stageInputLayout.setStartIconOnClickListener {
        stagesContainer.removeView(stageInputLayout)
        stagesContainer.children.filterIsInstance<TextInputLayout>()
            .forEachIndexed { index, stageLayout ->
                stageLayout.hint =
                    root.context.getString(R.string.stage_layout_hint).format(index + 1)
            }
    }

    stageInputLayout.setEndIconOnClickListener {
        illustrationInput.apply {
            visibility = when (visibility) {
                View.VISIBLE -> {
                    stageInputLayout.setEndIconDrawable(R.drawable.ic_show_image_24dp)
                    illustrationImageView.visibility = View.GONE
                    View.GONE
                }
                else -> {
                    stageInputLayout.setEndIconDrawable(R.drawable.ic_hide_image_24dp)
                    illustrationImageView.visibility = View.VISIBLE
                    View.VISIBLE
                }
            }
            setEndIconOnClickListener {
                illustrationImageView
                    .loadResOrURL(editText?.text.toString())
            }
        }
    }
    with(stageInputLayout) {
        hint = context.getString(R.string.stage_layout_hint).format(stagesContainer.size)
    }
    stageInputLayout.action()
    return stageInputLayout
}

internal fun FragmentCreateBinding.fillUpWithRecipe(
    recipeData: RecipeData,
    stages: List<CookingStage>,
    action: () -> Unit = {}
) {
    if (recipeData.title.isNotBlank()) {
        titleEdit.setText(recipeData.title)
        pictureSrcEdit.setText(recipeData.pictureSrc)
        cuisineCategoryActv.setText(recipeData.cuisine)
        cuisineCategory.apply {
            editText?.text?.let {
                if (it.isBlank()) return@apply
            } ?: return@apply
            setEndIconDrawable(R.drawable.ic_close_24)
            setEndIconOnClickListener {
                cuisineCategoryActv.text?.clear()
            }
        }
        ingredientsEdit.setText(recipeData.ingredients)
        recipeData.estimateTime.also {
            if (it >= 0) estimateTimeEdit.setText(it.toString())
        }
    }
    stages.forEachIndexed { index, cookingStage ->
        inflateNewStageLayout {
            if (index == 0) startIconDrawable = null
            editText?.setText(cookingStage.guidance)
            val illustrationInput =
                children.last { it is TextInputLayout } as TextInputLayout
            illustrationInput.editText?.setText(cookingStage.illustrationSrc)
        }
    }
    action
}

internal fun ViewGroup.inflateEmptyStateLayout(
    recipeInteractionListener: RecipeInteractionListener,
    fromFragmentTag: String,
    action: ViewGroup.() -> Unit = {}
) =
    run {
        View.inflate(context, R.layout.empty_state_layout, this)
        val emptyStateLayout = this.children.last() as ConstraintLayout
        emptyStateLayout.children.last { it is MaterialButton }
            .setOnClickListener {
                recipeInteractionListener.onRecipeClick(RecipeData.SANDWICH_ID, fromFragmentTag)
            }
        action()
        emptyStateLayout
    }

internal fun FragmentCreateBinding.buildRecipePair(recipeId: Long) =
    Pair(
        RecipeData(
            id = recipeId,
            title = title.editText?.text.toString(),
            author = "Current User",
            cuisine = cuisineCategory.editText?.text.toString(),
            estimateTime = try {
                estimateTime.editText?.text.toString().toInt()
            } catch (ex: java.lang.NumberFormatException) {
                RecipeData.TIME_NOT_SHOWN
            },
            pictureSrc = pictureSrc.editText?.text.toString(),
            ingredients = ingredients.editText?.text.toString()
        ),
        stagesContainer.children
            .filterIsInstance<TextInputLayout>()
            .mapIndexed { index, stageInputLayout ->
                CookingStage(
                    id = CookingStage.DEFAULT_STAGE_ID,
                    recipeId = recipeId,
                    turn = index + 1,
                    guidance = stageInputLayout.editText?.text.toString(),
                    illustrationSrc =
                    (stageInputLayout.children.last { it is TextInputLayout } as TextInputLayout)
                        .editText?.text.toString()
                )
            }.toList()
    )

internal fun FragmentAllBinding.switchFilterButtonActivated() {
    filtersButton.isActivated = filterGroup.chipGroup.checkedChipIds.isNotEmpty()
}

// region APP_CONSTANTS

const val COMMON_SHARED_PREFS_KEY = "commonPrefs"
const val FIRST_START_PREFS_KEY = "firstStart"


// endregion APP_CONSTANTS