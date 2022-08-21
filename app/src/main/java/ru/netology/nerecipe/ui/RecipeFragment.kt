package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.adapter.RecipeStagesAdapter
import ru.netology.nerecipe.databinding.FragmentRecipeBinding
import ru.netology.nerecipe.utils.renderRecipeCard
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

        binding.recipe.renderRecipeCard(recipeData)

        binding.stages.adapter = adapter

        viewModel.stagesQuery(recipeData.id)

        viewModel.currentStages.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { stages ->
                adapter.submitList(stages)
            }
        }

        return binding.root
    }
}