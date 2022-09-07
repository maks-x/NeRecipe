package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.FragmentCreateBinding
import ru.netology.nerecipe.utils.*
import ru.netology.nerecipe.viewModel.RecipeViewModel

class EditFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private lateinit var binding: FragmentCreateBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCreateBinding.inflate(inflater, container, false)
        val context = binding.root.context
        val textWatcher = CreateRecipeTextWatcher(binding)
        val cuisineArrayAdapter = ArrayAdapter(
            context,
            R.layout.text_view_layout,
            resources.getStringArray(R.array.cuisine_categories)
                .sorted()
        )
        binding.cuisineCategoryActv.setAdapter(cuisineArrayAdapter)
        val currentRecipeId = navArgs<EditFragmentArgs>().value.recipeId

        binding.createButton.setText(R.string.edit_recipe)

        viewModel.renderRecipeRequest(currentRecipeId)

        with(binding) {

            viewModel.recipeRenderingEvent.observe(viewLifecycleOwner) {
                if (it == null) {
                    binding.inflateNewStageLayout {
                        startIconDrawable = null
                    }
                    return@observe
                }
                binding.fillUpWithRecipe(it.first, it.second)
                textWatcher.initialize()
                textWatcher.afterTextChanged(null)
            }

            pictureSrc.setEndIconOnClickListener {
                mainPicture.loadResOrURL(pictureSrcEdit.text.toString())
            }

            createButton.setOnClickListener {
                val recipePair = buildRecipePair(currentRecipeId)
                recipePair.let {
                    viewModel.saveRecipe(it.first, it.second)
                }
                findNavController().navigateUp()
            }

            addStageButton.setOnClickListener {
                binding.inflateNewStageLayout {
                    editText?.addTextChangedListener(textWatcher)
                    textWatcher.afterTextChanged(null)
                }
            }
        }
        return binding.root
    }
}