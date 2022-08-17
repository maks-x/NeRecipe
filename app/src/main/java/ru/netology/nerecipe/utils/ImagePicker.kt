package ru.netology.nerecipe.utils

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner

class ImagePicker(
    private val activityResultRegistry: ActivityResultRegistry,
    private val lifecycleOwner: LifecycleOwner,
    private val callback: (imageUri: Uri?) -> Unit
) {
    private val getContent: ActivityResultLauncher<Array<String>> =
        activityResultRegistry.register(
            REGISTRY_KEY,
            lifecycleOwner,
            ActivityResultContracts.OpenDocument(),
            callback
        )

    fun pickImage() {
        getContent.launch(arrayOf(MIMETYPE_IMAGES))
    }

    companion object {
        const val REGISTRY_KEY = "ImagePicker"
        const val MIMETYPE_IMAGES = "image/*"
    }

}