package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipeStagesAdapter
import ru.netology.nerecipe.databinding.FragmentDetailsBinding
import ru.netology.nerecipe.obj.RecipeData
import ru.netology.nerecipe.utils.loadResOrURL
import ru.netology.nerecipe.viewModel.RecipeViewModel

class DetailsFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val adapter = RecipeStagesAdapter()
        val context = binding.root.context
        val recipeId = navArgs<DetailsFragmentArgs>().value.recipeId
        viewModel.renderRecipeRequest(recipeId)

        binding.stagesRegion.adapter = adapter

        viewModel.recipeRenderingEvent.observe(viewLifecycleOwner) {
            val recipePair = checkNotNull(it) {
                "There are no recipe with ID $recipeId!"
            }
            val recipeData = recipePair.first
            val stages = recipePair.second
            with(binding) {
                recipePicture.loadResOrURL(recipeData.pictureSrc)
                titleTextView.text = recipeData.title
                cuisineTextView.text = recipeData.cuisine
                authorTextView.text = recipeData.author
                ingredientsTextView.text = recipeData.ingredients
                timeTextView.text = context.getString(R.string.minutes).format(recipeData.estimateTime)
                titleTextView.text = recipeData.title
            }
            adapter.submitList(stages)
        }

        if (recipeId != RecipeData.SANDWICH_ID) {
            binding.menuDetails.setOnClickListener {
                PopupMenu(context, binding.menuDetails).apply {
                    inflate(R.menu.popup_recipe_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                viewModel.onRemoveClick(recipeId)
                                findNavController().navigateUp()
                                true
                            }
                            R.id.edit -> {
                                viewModel.onEditClick(recipeId, FROM_DETAILS_FRAGMENT_TAG)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
        viewModel.navigateFromDetailsToEditEvent.observe(viewLifecycleOwner) {
            val direction = DetailsFragmentDirections.detailsToEdit(it)
            findNavController().navigate(direction)
        }

        return binding.root
    }
    companion object {
        const val FROM_DETAILS_FRAGMENT_TAG = "from_details_fragment"
    }
}