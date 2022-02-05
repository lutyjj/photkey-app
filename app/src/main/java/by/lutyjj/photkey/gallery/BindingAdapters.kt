package by.lutyjj.photkey.gallery

import android.content.Context
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import by.lutyjj.photkey.R
import by.lutyjj.photkey.models.Photo
import coil.load

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val schemeRegex = "https|http".toRegex()
        val scheme = schemeRegex.find(it)?.value
        val imgUri = imgUrl.toUri().buildUpon().scheme(scheme).build()
        imgView.load(imgUri) {
            placeholder(getPlaceholder(imgView.context))
            error(R.drawable.ic_error)
        }
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Photo>?) {
    val adapter = recyclerView.adapter as PhotoGridAdapter
    adapter.submitList(data)
}

private fun getPlaceholder(context: Context): CircularProgressDrawable {
    val placeholder = CircularProgressDrawable(context)
    placeholder.strokeWidth = 5f
    placeholder.centerRadius = 30f
    placeholder.setColorSchemeColors(-1)
    return placeholder
}