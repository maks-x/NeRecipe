package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.FragmentCreateBinding
import ru.netology.nerecipe.obj.RecipeData
import ru.netology.nerecipe.utils.*
import ru.netology.nerecipe.viewModel.RecipeViewModel

class CreateFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private lateinit var binding: FragmentCreateBinding

    private var needDraft = true

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

        viewModel.renderRecipeRequest(RecipeData.DRAFT_ID)



        with(binding) {

            viewModel.recipeRenderingEvent.observe(viewLifecycleOwner) {
                if (it == null) {
                    inflateNewStageLayout {
                        startIconDrawable = null
                    }
                } else fillUpWithRecipe(it.first, it.second)
                textWatcher.initialize()
                textWatcher.afterTextChanged(null)
            }

            pictureSrc.setEndIconOnClickListener {
                mainPicture.loadResOrURL(pictureSrcEdit.text.toString())
            }

            createButton.setOnClickListener {
                val recipePair = buildRecipePair(RecipeData.DEFAULT_RECIPE_ID)
                recipePair.let {
                    viewModel.saveRecipe(it.first, it.second)
                }
                viewModel.clearDraft()
                needDraft = false
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

    override fun onDestroyView() {
        if (needDraft) viewModel.setDraft(binding.buildRecipePair(RecipeData.DRAFT_ID))
        super.onDestroyView()
    }
}