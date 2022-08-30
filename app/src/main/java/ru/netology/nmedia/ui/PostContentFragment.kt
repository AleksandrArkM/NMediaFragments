package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.databinding.PostContentFragmentBinding
import ru.netology.nmedia.util.focusAndShowKeyboard

class PostContentFragment : Fragment() {

    private val args by navArgs<PostContentFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PostContentFragmentBinding.inflate(
        layoutInflater, container, false
    ).also { binding ->

        binding.edit.setText(args.initialContent)

        binding.edit.focusAndShowKeyboard()

        binding.ok.setOnClickListener {
            onOkButtonClicked(binding)
        }
    }.root

    private fun onOkButtonClicked(binding: PostContentFragmentBinding) {
        val text = binding.edit.text

        if (!text.isNullOrBlank()) {
            val resultBundle = Bundle(1)
            resultBundle.putString(RESULT_KEY, text.toString())
            if (args.fromFragment == REQUEST_FEED_KEY) {
                setFragmentResult(
                    REQUEST_FEED_KEY,
                    resultBundle
                )
            } else if (args.fromFragment == REQUEST_CURRENT_POST_KEY) {
                setFragmentResult(
                    REQUEST_CURRENT_POST_KEY,
                    resultBundle
                )
            }
        }
        findNavController().popBackStack()
    }


    companion object {

        const val REQUEST_FEED_KEY = "requestForFeedFragmentKey"
        const val REQUEST_CURRENT_POST_KEY = "requestForCurrentPostFragmentKey"
        const val RESULT_KEY = "postNewContent"


    }
}