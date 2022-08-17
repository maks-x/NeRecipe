package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.nerecipe.adapter.RecipeFeedAdapter
import ru.netology.nerecipe.databinding.FragmentFavoritesBinding
import ru.netology.nerecipe.databinding.RecipesFeedBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FavoritesFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        val adapter = RecipeFeedAdapter(viewModel)

        binding.feed.recyclerView.adapter = adapter


        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }
}