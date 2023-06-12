package com.ninja.rickandmortyapp.utils

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ImageLoader {
    private val TAG = "ImageLoader"
    fun loadIntoImageView(context: Context, imageUrl: String, imageView: ImageView) {
        if(imageUrl.isNullOrEmpty()) {
            Log.w(TAG, "loadIntoImageView: ImageUrl is Null or Empty while Context=$context ImageUrl=$imageUrl ImageView=$imageView => no-op")
            return
        }
        Picasso.with(context).load(imageUrl).into(imageView)
    }
}