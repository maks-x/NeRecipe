package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipeStagesAdapter
import ru.netology.nerecipe.databinding.FragmentRecipeBinding
import ru.netology.nerecipe.utils.loadResOrURL
import ru.netology.nerecipe.viewModel.RecipeViewModel

class RecipeFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentRecipeBinding.inflate(inflater, container, false)

        val adapter = RecipeStagesAdapter()

        val recipeData = navArgs<RecipeFragmentArgs>().value.recipeData

        val context = binding.root.context

        with(binding) {

            recipePicture.loadResOrURL(recipeData.pictureSrc)
            titleTextView.text = recipeData.title
            cuisineTextView.text = recipeData.cuisine
            authorTextView.text = recipeData.author
            ingredientsTextView.text = recipeData.ingredients
            timeTextView.text = context.getString(R.string.minutes).format(recipeData.estimateTime)
            titleTextView.text = recipeData.title
        }

        binding.stagesRegion.adapter = adapter

        viewModel.renderStageRequest(recipeData.id)

        viewModel.stagesRenderingEvent.observe(viewLifecycleOwner) { stages ->
            adapter.submitList(stages)
        }

        return binding.root
    }
}