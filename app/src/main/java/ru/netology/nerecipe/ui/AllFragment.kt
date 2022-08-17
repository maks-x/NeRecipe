package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipeFeedAdapter
import ru.netology.nerecipe.databinding.FragmentAllBinding
import ru.netology.nerecipe.callback.RecipesDragAndDropCallback
import ru.netology.nerecipe.viewModel.RecipeViewModel

class AllFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private val navView by lazy {
        requireActivity().findViewById<BottomNavigationView>(R.id.nav_bottom_bar)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentAllBinding.inflate(inflater, container, false)

        val adapter = RecipeFeedAdapter(viewModel)

        binding.feed.recyclerView.adapter = adapter




        ItemTouchHelper(RecipesDragAndDropCallback(viewModel))
            .attachToRecyclerView(binding.feed.recyclerView)
//        val animator = DefaultItemAnimator()
//        animator.supportsChangeAnimations = false

        binding.feed.recyclerView.itemAnimator?.moveDuration = 500
        binding.feed.recyclerView.itemAnimator?.changeDuration = 500

//        (binding.feed.recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false


        navView.menu.getItem(1).setOnMenuItemClickListener {
//            navView.visibility = View.GONE
            val direction =
                AllFragmentDirections.actionNavigationRecipesToNavigationAdd()
            findNavController().navigate(direction)
            true
        }

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.recipeClickedEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { recipe ->
                val direction =
                    AllFragmentDirections.actionNavigationRecipesToRecipeFragment(recipe)
                findNavController().navigate(direction)
            }

        }



        return binding.root
    }
}