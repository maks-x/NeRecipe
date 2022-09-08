package ru.netology.nerecipe.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import ru.netology.nerecipe.adapter.RecipeFeedAdapter
import ru.netology.nerecipe.callback.RecipesDragAndDropCallback
import ru.netology.nerecipe.databinding.FragmentAllBinding
import ru.netology.nerecipe.utils.inflateEmptyStateLayout
import ru.netology.nerecipe.utils.switchFilterButtonActivated
import ru.netology.nerecipe.utils.switchVisibility
import ru.netology.nerecipe.viewModel.RecipeViewModel

class AllFragment : Fragment(), TextWatcher {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private lateinit var binding: FragmentAllBinding

    private var emptyStateLayout: ConstraintLayout? = null
    private var filteredEmptyStateLayout: ConstraintLayout? = null
    private val feedContainer
        get() = binding.feed.feedContainer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAllBinding.inflate(inflater, container, false)
        val adapter = RecipeFeedAdapter(viewModel, FROM_ALL_FRAGMENT_TAG)
        with(binding) {
            feed.recyclerView.adapter = adapter
            searchInput.editText?.apply {
                setText(viewModel.currentTitleFilter)
                addTextChangedListener(this@AllFragment)
            }
            ItemTouchHelper(
                RecipesDragAndDropCallback { from, to ->
                    viewModel.onReplaceRecipeCard(from, to)
                }).attachToRecyclerView(feed.recyclerView)

            viewModel.data.observe(viewLifecycleOwner) { recipeDataList ->
                val isNewRecipeAdded = recipeDataList.size > adapter.currentList.size
                adapter.submitList(recipeDataList) {
                    if (recipeDataList.isEmpty() && emptyStateLayout == null) {
                        emptyStateLayout =
                            feedContainer.inflateEmptyStateLayout(
                                viewModel,
                                FROM_ALL_FRAGMENT_TAG
                            )
                    }
                    if (recipeDataList.isNotEmpty()) {
                        emptyStateLayout?.let { feedContainer.removeView(it) }
                        emptyStateLayout = null
                    }
                    if (isNewRecipeAdded) feed.recyclerView.apply {
                        scrollToPosition(top)
                    }
                }
            }

            viewModel.filterEvent.observe(viewLifecycleOwner) { filteredList ->
                adapter.submitList(filteredList) {
                    emptyStateLayout?.let { feedContainer.removeView(it) }
                    if (filteredList.isEmpty() && filteredEmptyStateLayout == null) {
                        filteredEmptyStateLayout =
                            feedContainer.inflateEmptyStateLayout(
                                viewModel,
                                FROM_ALL_FRAGMENT_TAG
                            )
                    }
                    if (filteredList.isNotEmpty()) {
                        filteredEmptyStateLayout?.let { feedContainer.removeView(it) }
                        filteredEmptyStateLayout = null
                    }
                }

            }

            filtersButton.setOnClickListener {
                filterGroup.root.switchVisibility()
            }

            filterGroup.submitButton.setOnClickListener {
                filterGroup.root.switchVisibility()
                val filters = filterGroup.chipGroup.checkedChipIds
                switchFilterButtonActivated()
                viewModel.submitCuisineFilter(filters)
            }

            filterGroup.clearButton.setOnClickListener {
                filterGroup.chipGroup.clearCheck()
            }
        }

        viewModel.onAllFragmentRecipeClickedEvent.observe(viewLifecycleOwner) {
            val direction =
                AllFragmentDirections.actionNavigationRecipesToRecipeFragment(it)
            findNavController().navigate(direction)
        }

        viewModel.navigateToCreateFragmentEvent.observe(viewLifecycleOwner) {
            val direction = AllFragmentDirections.allToEdit(it)
            findNavController().navigate(direction)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.switchFilterButtonActivated()
    }

    //region TextWatcher

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //do nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //do nothing
    }

    override fun afterTextChanged(s: Editable?) {
        val titleFilter = s.toString()
        viewModel.submitTitleFilter(titleFilter)
    }

    //endregion TextWatcher
    companion object {
        const val FROM_ALL_FRAGMENT_TAG = "from_all_fragment"
    }
}