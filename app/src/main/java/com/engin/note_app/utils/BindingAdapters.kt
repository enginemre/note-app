package com.engin.note_app.utils

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.engin.note_app.R
import com.engin.note_app.domain.model.Note


/**
 *
 * Changing visibility of view according to [show]
 *
 * @param show bool parameter true means show view
 *
 */
@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
    view.visibility = (if (show) View.GONE else View.VISIBLE)
}

/**
 *
 * Binding function that loading image from url
 * Using Glide
 * @param imageUrl image url
 *
 *
 */
@SuppressLint("CheckResult")
@BindingAdapter("loadFromUrl")
fun ImageView.loadImageFromURL(imageUrl: String?) {
    if (imageUrl != null && imageUrl.isNotEmpty()) {
        try {
            // The attributes for a ShimmerDrawable is set by this builder
            val shimmer = Shimmer.AlphaHighlightBuilder()
                .setDuration(1800)
                .setBaseAlpha(0.7f)
                .setHighlightAlpha(0.6f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build()

            // This is the placeholder for the imageView
            val shimmerDrawable = ShimmerDrawable().apply {
                setShimmer(shimmer)
            }

            Glide.with(this).load(imageUrl).let { requestBuilder ->
                requestBuilder.placeholder(shimmerDrawable)
                requestBuilder.error(R.drawable.ic_baseline_image_not_supported)
                requestBuilder.into(this)
            }
        }catch (e : Exception){
            // Image does not load from URL

            e.printStackTrace()
            this.setImageResource(R.drawable.ic_baseline_image_not_supported)
        }

    } else {
        // No image
        this.visibility = View.GONE
    }
}

/**
 *
 * Hiding update data according to update status
 * @param item  note item
 *
 */
@BindingAdapter("hideView")
fun shouldShowView(view: View, item : Note) {
    view.visibility = (if (item.updatedDateToString().isNotEmpty()) View.VISIBLE else View.GONE)
}





