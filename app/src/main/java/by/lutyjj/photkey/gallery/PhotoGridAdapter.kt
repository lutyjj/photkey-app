package by.lutyjj.photkey.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.lutyjj.photkey.MainActivity
import by.lutyjj.photkey.databinding.GridViewItemBinding
import by.lutyjj.photkey.models.Photo

class PhotoGridAdapter : ListAdapter<Photo, PhotoGridAdapter.PhotoViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoViewHolder {
        return PhotoViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = getItem(position)
        val apiPath = MainActivity.getApiPath()
        photo.imgUrl = "${apiPath}/photos/${photo.name}"
        holder.bind(photo)
    }

    class PhotoViewHolder(private var binding: GridViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(Photo: Photo) {
            binding.photo = Photo
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.name == newItem.name
        }

    }
}