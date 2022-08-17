package ru.netology.nerecipe.callback

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.viewModel.RecipeInteractionListener

class RecipesDragAndDropCallback(private val interactionListener: RecipeInteractionListener) :
    ItemTouchHelper.SimpleCallback(UP or DOWN, 0) {

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.5f
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val from = viewHolder.absoluteAdapterPosition
        val to = target.absoluteAdapterPosition
        interactionListener.onReplaceRecipeView(from, to)
        //TODO на этом этапе при перетаскивании самого верхнего видимого адаптера, очень велика
        // скорость анимации скролла. Как с этим бороться? Различные комбинации
        // notifyItem/Range/DataSetChanged c onReplaceRecipeView() вообще делают анимацию
        // малопредсказуемой для пользователя

        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1.0f
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

}