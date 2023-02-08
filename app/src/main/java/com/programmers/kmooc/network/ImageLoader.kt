package com.programmers.kmooc.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

object ImageLoader {

    private val imageCache = mutableMapOf<String, Bitmap>()

    fun loadImage(url: String, completed: (Bitmap?) -> Unit) {
        //TODO: String -> Bitmap 을 구현하세요

        if (url.isEmpty()) {
            completed(null)
            return
        }

        if (imageCache.containsKey(url)) {
            completed(imageCache[url])
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeStream(URL(url).openStream())
            withContext(Dispatchers.Main) {
                try {
                    imageCache[url] = bitmap
                    completed(bitmap)
                } catch (e: Exception) {
                    completed(null)
                }
            }
        }
    }
}