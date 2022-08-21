package ru.netology.nerecipe.ui

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.load
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.FragmentCreateBinding
import ru.netology.nerecipe.utils.CreateRecipeTextWatcher
import ru.netology.nerecipe.utils.ImagePicker
import ru.netology.nerecipe.viewModel.RecipeViewModel

class CreateFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private lateinit var binding: FragmentCreateBinding

    private lateinit var imgPicker: ImagePicker

    @OptIn(ExperimentalCoilApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imgPicker = ImagePicker(requireActivity().activityResultRegistry, this) {
            binding.mainPicture.load(it)
            println("It uri:$it")
            Handler().postDelayed(
                {
                    requireContext().imageLoader.diskCache
                        ?.get(it.toString())
                        ?.use { snapshot ->
                            val imageFile = snapshot.data.toFile()
                            println("It key:${imageFile.absolutePath}")
                            // Read or copy the file. You **must** close the snapshot (`use` closes the snapshot)
                            // or it'll prevent writing to that entry until your app is killed.
                        }
                }, 5000//
            )
        }
    }

    @OptIn(ExperimentalCoilApi::class, ExperimentalCoilApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCreateBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val context = binding.root.context
        val cuisineAdapter = ArrayAdapter(
            context,
            R.layout.text_view_layout,
            resources.getStringArray(R.array.cuisine_categories)
                .sorted()
        )
        val aCTView = (binding.cuisineCategory.editText as? AutoCompleteTextView)
        aCTView?.setAdapter(cuisineAdapter)

        CreateRecipeTextWatcher(binding).initialize()

        binding.pictureSrc.editText?.let {
            it.setText("https://berserkon.com/images/nachos-clipart-beef-nacho-4.png")
            it.isEnabled = false
        }

        binding.createButton.setOnClickListener {
            imgPicker.pickImage()
        }
//            viewModel.setLinkValue("content://com.android.providers.downloads.documents/document/msf%3A31")

//            val imageLoader = ImageLoader.Builder(context)
//                .diskCache {
//                DiskCache.Builder()
//                    .directory(context.cacheDir.resolve("recipe_images"))
//                    .build()
//            }.build()
//
//            val view =
//                requireActivity().findViewById<com.google.android.material.imageview.ShapeableImageView>(
//                    R.id.main_picture
//                )
//
//
//            view
//                .load(
//                    "https://i.ytimg.com/vi/35F6v3GOKnU/hqdefault.jpg",
//                    imageLoader
//                )
//
//                Handler().postDelayed(
//                    {
//                        val newView = requireActivity().findViewById<com.google.android.material.imageview.ShapeableImageView>(
//                            R.id.main_picture)
//                        println("It key:${newView.result?.request?.diskCacheKey}") },
//                5000
//                )

        return binding.root
    }
}