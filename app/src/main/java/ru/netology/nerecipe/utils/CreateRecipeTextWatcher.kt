package ru.netology.nerecipe.utils

import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.children
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import ru.netology.nerecipe.databinding.FragmentCreateBinding

class CreateRecipeTextWatcher(private val binding: FragmentCreateBinding) : TextWatcher {

    private val listEditText
        get() = binding.dataContainer.children
            .plus(binding.stagesContainer.children)
            .filterIsInstance<TextInputLayout>()
            .map { it.editText }
            .filterNotNull()
            .toList()

    private val createButton: MaterialButton
        get() = binding.createButton

    fun initialize() {
        listEditText.forEach {
            it.addTextChangedListener(this)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        listEditText.forEach { editText ->
            if (editText.text.isNullOrBlank()) {
                createButton.isEnabled = false
                return
            }
        }
        createButton.isEnabled = true
    }
}