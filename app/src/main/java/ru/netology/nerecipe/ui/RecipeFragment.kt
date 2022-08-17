package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.adapter.RecipeFeedAdapter
import ru.netology.nerecipe.adapter.RecipeStagesAdapter
import ru.netology.nerecipe.databinding.FragmentAllBinding
import ru.netology.nerecipe.databinding.FragmentRecipeBinding
import ru.netology.nerecipe.databinding.RecipeCardBinding
import ru.netology.nerecipe.databinding.RecipesFeedBinding
import ru.netology.nerecipe.utils.renderRecipe
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

        val adapter = RecipeStagesAdapter(viewModel)

        val recipe = navArgs<RecipeFragmentArgs>().value.recipe

        binding.recipe.renderRecipe(recipe)

        binding.stages.adapter = adapter

        adapter.submitList(recipe.stages)

        return binding.root
    }
}