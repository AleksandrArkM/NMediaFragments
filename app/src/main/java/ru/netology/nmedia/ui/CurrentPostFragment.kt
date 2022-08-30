package ru.netology.nmedia.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.Post.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.data.App
import ru.netology.nmedia.databinding.CurrentPostFragmentBinding
import ru.netology.nmedia.viewModel.PostViewModel

class CurrentPostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    private val args by navArgs<CurrentPostFragmentArgs>()

    private lateinit var currentPost: Post

    private val Fragment.packageManager
        get() = activity?.packageManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return CurrentPostFragmentBinding.inflate(
            layoutInflater, container, false
        ).also { binding ->
            with(binding) {

                currentPost = viewModel.data.value?.let { listPost ->
                    listPost.firstOrNull {
                        it.id == args.idCurrentPost
                    }
                } as Post
                render(currentPost)


                viewModel.data.observe(viewLifecycleOwner) { listPost ->
                    if (listPost.none { it.id == args.idCurrentPost } ) {
                        return@observe
                    }
                    currentPost = listPost.firstOrNull {
                        it.id == args.idCurrentPost
                    } as Post
                    render(currentPost)
                }

                viewModel.sharePostContent.observe(viewLifecycleOwner) { postContent ->
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, postContent)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(
                        intent, getString(R.string.chooser_share_post)
                    )
                    startActivity(shareIntent)
                }

                viewModel.playVideo.observe(viewLifecycleOwner) { videoUrl ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                    if (packageManager?.let { intent.resolveActivity(it) } != null) {
                        startActivity(intent)
                    }
                }

                viewModel
                    .navigateToPostContentScreenEvent
                    .observe(viewLifecycleOwner) { initialContent ->
                        val direction =
                            CurrentPostFragmentDirections.currentPostFragmentToPostContentFragment(
                                initialContent, PostContentFragment.REQUEST_CURRENT_POST_KEY
                            )
                        findNavController().navigate(direction)
                    }

                setFragmentResultListener(
                    requestKey = PostContentFragment.REQUEST_CURRENT_POST_KEY
                ) { requestKey, bundle ->
                    if (requestKey != PostContentFragment.REQUEST_CURRENT_POST_KEY) return@setFragmentResultListener
                    val newPostContent = bundle.getString(
                        PostContentFragment.RESULT_KEY
                    ) ?: return@setFragmentResultListener
                    viewModel.onSaveButtonClicked(newPostContent)
                }

                likeIcon.setOnClickListener {
                    viewModel.onLikeClicked(currentPost)
                }

                shareIcon.setOnClickListener {
                    viewModel.onShareClicked(currentPost)
                }

                binding.videoBanner.setOnClickListener {
                    viewModel.onPlayVideoClicked(currentPost)
                }
                binding.playVideo.setOnClickListener {
                    viewModel.onPlayVideoClicked(currentPost)
                }

                val popupMenu by lazy {
                    PopupMenu(context, binding.options).apply {
                        inflate(R.menu.options_post)
                        setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.remove -> {
                                    viewModel.onRemoveClicked(currentPost)
                                    findNavController().popBackStack()
                                    true
                                }
                                R.id.edit -> {
                                    viewModel.onEditClicked(currentPost)
                                    true
                                }
                                else -> false
                            }
                        }
                    }
                }

                binding.options.setOnClickListener {
                    popupMenu.show()
                }

            }
        }.root
    }

    private fun CurrentPostFragmentBinding.render(post: Post) {
        authorName.text = post.author
        content.text = post.content
        date.text = post.published
        likeIcon.text = displayAmountIntToString(post.likes)
        shareIcon.text = displayAmountIntToString(post.shareCount)
        content.movementMethod = ScrollingMovementMethod()
        likeIcon.isChecked = post.likedByMe
        videoGroup.isVisible = post.video != null
    }

    private fun displayAmountIntToString(i: Int): String {
        val str = i.toString()
        return when (i) {
            in 1000..1099 -> "1${App.instance.getString(R.string.kilo)}"
            in 1100..9999 -> "${str[0]}.${str[1]}${App.instance.getString(R.string.kilo)}"
            in 10000..99999 -> "${str[0]}${str[1]}${App.instance.getString(R.string.kilo)}"
            in 100000..999999 -> "${str[0]}${str[1]}${str[2]}${App.instance.getString(R.string.kilo)}"
            in 1000000..1099999 -> "${str[0]}${App.instance.getString(R.string.million)}"
            in 1100000..999999999 -> "${str[0]}.${str[1]}${App.instance.getString(R.string.million)}"
            else -> "$i"
        }
    }

}