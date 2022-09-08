package ru.netology.nerecipe.callback

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.adapter.RecipeFeedAdapter
import kotlin.properties.Delegates

class RecipesDragAndDropCallback(private val changeDataAfterMove: (fromNumber: Long, toNumber: Long) -> Unit) :
    ItemTouchHelper.SimpleCallback(UP or DOWN, 0) {

    private var fromOrdinal by Delegates.notNull<Long>()
    private var toOrdinal: Long? = null

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.5f
        }
        viewHolder?.let {
            fromOrdinal = (it as RecipeFeedAdapter.RecipesViewHolder).recipeOrdinal
        }

    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPos = viewHolder.absoluteAdapterPosition
        val toPos = target.absoluteAdapterPosition
        val adapter = recyclerView.adapter
        adapter?.notifyItemMoved(fromPos, toPos)
        toOrdinal = (target as RecipeFeedAdapter.RecipesViewHolder).recipeOrdinal

//        adapter?.notifyItemChanged(fromPos)
//        adapter?.notifyItemChanged(toPos)

        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1.0f
        toOrdinal?.let { toOrdinal ->
            changeDataAfterMove(fromOrdinal, toOrdinal)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

}