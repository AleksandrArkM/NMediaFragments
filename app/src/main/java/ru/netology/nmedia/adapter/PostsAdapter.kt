package ru.netology.nmedia.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.Post.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.data.App
import ru.netology.nmedia.databinding.PostBinding


internal class PostsAdapter(
    private val interactionListener: PostInteractionListener
) : ListAdapter<Post, PostsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: PostBinding,
        listener: PostInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var post: Post


        private val popupMenu by lazy {
            PopupMenu(itemView.context, binding.options).apply {
                inflate(R.menu.options_post)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.remove -> {
                            listener.onRemoveClicked(post)
                            true
                        }
                        R.id.edit -> {
                            listener.onEditClicked(post)

                            true
                        }
                        else -> false
                    }
                }
            }
        }

        init {
            binding.likeIcon.setOnClickListener { listener.onLikeClicked(post) }
            binding.shareIcon.setOnClickListener { listener.onShareClicked(post) }
            binding.videoBanner.setOnClickListener { listener.onPlayVideoClicked(post) }
            binding.playVideo.setOnClickListener { listener.onPlayVideoClicked(post) }
            binding.options.setOnClickListener { popupMenu.show() }
            binding.root.setOnClickListener { listener.onPostClicked(post) }
        }

        fun bind(post: Post) {
            this.post = post

            with(binding) {
                authorName.text = post.author
                content.text = post.content
                date.text = post.published
                likeIcon.text = displayAmountIntToString(post.likes)
                shareIcon.text = displayAmountIntToString(post.shareCount)
                likeIcon.isChecked = post.likedByMe
                videoGroup.isVisible = post.video != null
            }
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

    private object DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: Post, newItem: Post) =
            oldItem == newItem

    }
}