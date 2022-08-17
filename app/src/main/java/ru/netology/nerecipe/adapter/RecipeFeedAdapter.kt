package ru.netology.nerecipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.databinding.RecipeCardBinding
import ru.netology.nerecipe.obj.Recipe
import ru.netology.nerecipe.utils.renderRecipe
import ru.netology.nerecipe.viewModel.RecipeInteractionListener

class RecipeFeedAdapter(
    private val interactionListener: RecipeInteractionListener
) : ListAdapter<Recipe, RecipeFeedAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val recipeCardBinding = RecipeCardBinding.inflate(
            inflater, parent, false
        )
        return ViewHolder(recipeCardBinding, interactionListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val recipeCardBinding: RecipeCardBinding,
        listener: RecipeInteractionListener
    ) : RecyclerView.ViewHolder(recipeCardBinding.root) {

        private lateinit var recipe: Recipe

        init {
            with(recipeCardBinding) {
                recipeArea.setOnClickListener {
                    listener.onRecipeClick(recipe)
                }
            }
        }

        fun bind(recipe: Recipe) {
            this.recipe = recipe
            recipeCardBinding.renderRecipe(recipe)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem == newItem

    }
}