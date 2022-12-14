package com.example.kavyasynechrontask.common.fetchimage.strategy

import android.graphics.Bitmap
import com.example.kavyasynechrontaskapp.common.fetchimage.exception.UnsuccessfulImageRequest


import com.example.kavyasynechrontask.common.fetchimage.target.TargetSize
import com.example.kavyasynechrontask.common.network.MakeRequest
import com.example.kavyasynechrontask.common.fetchimage.cache.ImageCache
import com.example.kavyasynechrontask.common.fetchimage.decode.ImageDecoder
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.InputStream
import java.util.concurrent.ExecutorService

interface ImageLoadStrategy {

    fun proceed(
        url: String,
        targetSize: TargetSize,
        onResult: (result: Bitmap?) -> Unit,
        onBackwardResult: (result: Bitmap) -> Unit = {}
    )

    class Memory(
        private val memoryCache: ImageCache.Memory,
        private val next: ImageLoadStrategy? = null
    ) : ImageLoadStrategy {
        override fun proceed(
            url: String,
            targetSize: TargetSize,
            onResult: (result: Bitmap?) -> Unit,
            onBackwardResult: (result: Bitmap) -> Unit
        ) {
            val memoryCachedImage = memoryCache.get(url)
            if (memoryCachedImage != null) {
                Timber.d("From memory: $url")
                onResult.invoke(memoryCachedImage)
                return
            }

            next?.proceed(url, targetSize, onResult) { result ->
                memoryCache.store(url, result)
            } ?: onResult.invoke(memoryCachedImage)
        }
    }

    class Persistence(
        private val persistenceCache: ImageCache.Persistence,
        private val executorService: ExecutorService,
        private val next: ImageLoadStrategy? = null
    ) : ImageLoadStrategy {

        override fun proceed(
            url: String,
            targetSize: TargetSize,
            onResult: (result: Bitmap?) -> Unit,
            onBackwardResult: (result: Bitmap) -> Unit
        ) {
            executorService.submit {
                val persistenceCacheImage = persistenceCache.get(url)
                if (persistenceCacheImage != null) {
                    Timber.d("From storage: $url")
                    onBackwardResult.invoke(persistenceCacheImage)
                    onResult.invoke(persistenceCacheImage)
                    return@submit
                }

                next?.proceed(url, targetSize, onResult) { result ->
                    onBackwardResult.invoke(result)
                    persistenceCache.store(url, result)
                } ?: onResult.invoke(persistenceCacheImage)
            }
        }
    }

    class Remote(
        okHttpClient: OkHttpClient,
        private val streamImageDecoder: ImageDecoder<InputStream>
    ) : ImageLoadStrategy, MakeRequest(okHttpClient) {

        override fun proceed(
            url: String,
            targetSize: TargetSize,
            onResult: (result: Bitmap?) -> Unit,
            onBackwardResult: (result: Bitmap) -> Unit
        ) {
            makeRequest(url = url,
                onResult = { response ->
                    response.body!!.byteStream().use { imageStream ->
                        val remoteImage = streamImageDecoder.decode(
                            imageStream,
                            targetSize
                        )
                        Timber.d("From network: $url")
                        if (remoteImage != null) {
                            onBackwardResult.invoke(remoteImage)
                        }
                        onResult.invoke(remoteImage)
                    }
                }, onError = {
                    throw UnsuccessfulImageRequest(url)
                }
            )
        }
    }
}