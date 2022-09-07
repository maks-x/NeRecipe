package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.adapter.RecipeFeedAdapter
import ru.netology.nerecipe.databinding.FragmentFavoritesBinding
import ru.netology.nerecipe.utils.inflateEmptyStateLayout
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FavoritesFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private var emptyStateLayout: ConstraintLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        val adapter = RecipeFeedAdapter(viewModel, FROM_FAVORITES_FRAGMENT_TAG)

        binding.feed.recyclerView.adapter = adapter


        viewModel.favoriteRecipes.observe(viewLifecycleOwner) { recipeDataList ->
            adapter.submitList(recipeDataList) {
                if (recipeDataList.isEmpty()) {
                    emptyStateLayout =
                        binding.root.inflateEmptyStateLayout(viewModel, FROM_FAVORITES_FRAGMENT_TAG)
                    return@submitList
                }
                emptyStateLayout?.let { binding.root.removeView(it) }
            }
        }

        viewModel.onFavoritesFragmentRecipeClickedEvent.observe(viewLifecycleOwner) { recipeData ->
            val direction =
                FavoritesFragmentDirections.actionNavigationFavoritesToRecipeFragment(recipeData)
            findNavController().navigate(direction)
        }

        viewModel.navigateFromFavoritesToEditEvent.observe(viewLifecycleOwner) {
            val direction = FavoritesFragmentDirections.favoritesToCreate(it)
            findNavController().navigate(direction)
        }

        return binding.root
    }

    companion object {
        const val FROM_FAVORITES_FRAGMENT_TAG = "from_favorites_fragment"
    }
}