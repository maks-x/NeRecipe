package ru.netology.nerecipe.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.chip.Chip
import ru.netology.nerecipe.adapter.RecipeFeedAdapter
import ru.netology.nerecipe.callback.RecipesDragAndDropCallback
import ru.netology.nerecipe.databinding.FragmentAllBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class AllFragment : Fragment(), TextWatcher {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentAllBinding.inflate(inflater, container, false)

        val adapter = RecipeFeedAdapter(viewModel)

        binding.feed.recyclerView.adapter = adapter

        ItemTouchHelper(
            RecipesDragAndDropCallback { from, to ->
                viewModel.onReplaceRecipeCard(from, to)
            }).attachToRecyclerView(binding.feed.recyclerView)

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.searchInput
                .apply { clearFocus() }
                .editText?.text?.clear()
        }

        viewModel.recipeClickedEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { recipeData ->
                val direction =
                    AllFragmentDirections.actionNavigationRecipesToRecipeFragment(recipeData)
                findNavController().navigate(direction)
            }

        }

        binding.searchInput.editText?.addTextChangedListener(this)
        viewModel.filteredEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { list ->
                adapter.submitList(list)
            }
        }

        binding.filtersButton.setOnClickListener {
            with(binding.filterLayout) {
                visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
            }
        }

        binding.filterGroup.chipGroup.checkedChipIds.let { chipIds ->
            val listChips: MutableList<Chip> = mutableListOf()
            chipIds.forEach { chipId ->
                listChips.add(requireActivity().findViewById(chipId))
            }
            listChips
        }.forEach {
            it.setOnCheckedChangeListener { _, isChecked ->
                val cuisine = it.text.toString()
                if (isChecked) {
                    viewModel.showCuisine(cuisine) { recipes ->
                        adapter.submitList(recipes)
                    }
                } else {
                    viewModel.hideCuisine(cuisine) { recipes ->
                        adapter.submitList(recipes)
                    }
                }
            }
        }

        return binding.root
    }

    //region TextWatcher

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //do nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //do nothing
    }

    override fun afterTextChanged(s: Editable?) {
        viewModel.dataFilteredBy { it.title.lowercase().contains(s.toString().lowercase())}
    }
    //endregion TextWatcher
}