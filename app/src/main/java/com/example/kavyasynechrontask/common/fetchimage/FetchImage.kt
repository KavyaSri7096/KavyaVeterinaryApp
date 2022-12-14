package com.example.kavyasynechrontask.common.fetchimage

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import com.example.kavyasynechrontask.common.fetchimage.strategy.ImageLoadStrategy
import com.example.kavyasynechrontask.common.fetchimage.target.GetTargetSize
import com.example.kavyasynechrontaskapp.common.ui.UiExecutor
import timber.log.Timber


interface FetchImage {

    @MainThread
    fun into(view: ImageView, url: String, onError: (e: Throwable) -> Unit = {})

    @MainThread
    fun cancel(view: ImageView)

    /**
     * TODOs:
     * 1. Request cancellation
     * 2. Check if request already running-in
     */
    class Base(
        @DrawableRes private val placeholder: Int,
        private val loadStrategy: ImageLoadStrategy,
        private val getTargetSize: GetTargetSize<View>,
        private val uiExecutor: UiExecutor = UiExecutor.Main()
    ) : FetchImage {

        @MainThread
        override fun into(view: ImageView, url: String, onError: (e: Throwable) -> Unit) {
            try {
                getTargetSize.onSizeReady(view) { targetSize ->
                    loadStrategy.proceed(url, targetSize, { image ->
                        if (image != null) {
                            uiExecutor.execute { view.setImageBitmap(image) }
                        } else {
                            uiExecutor.execute { view.setImageResource(placeholder) }
                        }
                    })
                }
            } catch (e: Exception) {
                onError.invoke(e)
                Timber.e(e)
            }
        }

        @MainThread
        override fun cancel(view: ImageView) {

        }
    }
}