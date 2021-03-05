package com.moviesdetailapp.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.moviesdetailapp.R
import com.moviesdetailapp.interfaces.OnBitmapLoad

class GlideHelper {
    companion object {
        fun loadImg(context: Context, url: String, imageView: ImageView, listener: OnBitmapLoad?) {
            val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(context).asBitmap().load(url).apply(requestOptions)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.place_h))
                .error(ContextCompat.getDrawable(context, R.drawable.error_loading_place_h))
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imageView.setImageBitmap(resource)
                        listener?.bitmapLoaded(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }
    }
}