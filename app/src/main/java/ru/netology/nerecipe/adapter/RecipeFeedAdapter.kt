package ru.netology.nerecipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.RecipeCardBinding
import ru.netology.nerecipe.obj.RecipeData
import ru.netology.nerecipe.utils.renderRecipeCard
import ru.netology.nerecipe.viewModel.RecipeInteractionListener

class RecipeFeedAdapter(
    private val interactionListener: RecipeInteractionListener,
    private val fomFragmentTag: String
) : ListAdapter<RecipeData, RecipeFeedAdapter.RecipesViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val recipeCardBinding = RecipeCardBinding.inflate(
            inflater, parent, false
        )
        return RecipesViewHolder(recipeCardBinding, interactionListener)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipesViewHolder(
        private val recipeCardBinding: RecipeCardBinding,
        listener: RecipeInteractionListener
    ) : RecyclerView.ViewHolder(recipeCardBinding.root) {

        private lateinit var recipeData: RecipeData
        val recipeOrdinal
            get() = recipeData.ordinal

        private val popupMenu by lazy {
            PopupMenu(itemView.context, recipeCardBinding.menu).apply {
                inflate(R.menu.popup_recipe_options)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            listener.onRemoveClick(recipeData.id)
                            true
                        }
                        R.id.edit -> {
                            listener.onEditClick(recipeData.id, fomFragmentTag)
                            true
                        }
                        else -> false
                    }
                }
            }
        }

        init {
            with(recipeCardBinding) {
                menu.setOnClickListener { popupMenu.show() }
                favorites.setOnClickListener {
                    listener.onAddToFavoritesClick(recipeData.id)
                }
                recipeArea.setOnClickListener {
                    listener.onRecipeClick(recipeData.id, fomFragmentTag)
                }
            }
        }

        fun bind(recipeData: RecipeData) {
            this.recipeData = recipeData
            recipeCardBinding.renderRecipeCard(recipeData)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<RecipeData>() {
        override fun areItemsTheSame(oldItem: RecipeData, newItem: RecipeData) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RecipeData, newItem: RecipeData) =
            oldItem == newItem
    }
}