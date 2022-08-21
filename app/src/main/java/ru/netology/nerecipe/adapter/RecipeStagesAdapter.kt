package ru.netology.nerecipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.databinding.RecipeStageBinding
import ru.netology.nerecipe.obj.CookingStage
import ru.netology.nerecipe.utils.renderStage

class RecipeStagesAdapter()
    : ListAdapter<CookingStage, RecipeStagesAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val recipeStageBinding = RecipeStageBinding.inflate(
            inflater, parent, false
        )
        return ViewHolder(recipeStageBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val recipeStageBinding: RecipeStageBinding
    ) : RecyclerView.ViewHolder(recipeStageBinding.root) {

        private lateinit var stage: CookingStage

        fun bind(stage: CookingStage) {
            this.stage = stage
            recipeStageBinding.renderStage(stage)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<CookingStage>() {
        override fun areItemsTheSame(oldItem: CookingStage, newItem: CookingStage) =
            oldItem.recipeId == newItem.recipeId

        override fun areContentsTheSame(oldItem: CookingStage, newItem: CookingStage) =
            oldItem == newItem

    }
}