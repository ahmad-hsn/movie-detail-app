package com.moviesdetailapp.fragment

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.moviesdetailapp.R
import com.moviesdetailapp.helper.Constants
import com.moviesdetailapp.helper.GlideHelper
import com.moviesdetailapp.interfaces.OnBitmapLoad

class PagerFragment : Fragment(), OnBitmapLoad {
    private lateinit var imageView: ImageView
    private lateinit var onBitmapLoad: OnBitmapLoad

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view?.findViewById(R.id.imageView)
        GlideHelper.loadImg(activity!!, Constants.IMG_BASE_URL + arguments?.getString(Constants.DATA_KEY)!!, imageView, this)
    }

    override fun bitmapLoaded(bitmap: Bitmap) {
        if(this::onBitmapLoad.isInitialized) onBitmapLoad.bitmapLoaded(bitmap)
    }

    fun onBitmapLoaded(onBitmapLoad: OnBitmapLoad){
        this.onBitmapLoad = onBitmapLoad
    }
}