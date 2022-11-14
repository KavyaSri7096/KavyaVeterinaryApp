package com.example.kavyasynechrontask.di

import android.content.Context

import com.example.kavyasynechrontask.common.fetchimage.FetchImage
import com.example.kavyasynechrontask.common.network.HttpService
import com.example.kavyasynechrontask.common.ui.UseCase
import com.example.kavyasynechrontaskapp.data.cloud.*
import com.example.kavyasynechrontaskapp.di.DiContainer
import com.example.kavyasynechrontaskapp.domain.ConfigDomain
import com.example.kavyasynechrontaskapp.domain.FetchConfigUseCase
import com.example.kavyasynechrontaskapp.domain.FetchPetsUseCase
import com.example.kavyasynechrontaskapp.domain.PetDomain
import com.example.kavyasynechrontaskapp.domain.workinghours.CheckWorkHours
import com.example.kavyasynechrontaskapp.domain.workinghours.CurrentDate
import com.example.kavyasynechrontaskapp.ui.list.PetListMapper
import com.example.kavyasynechrontask.R
import com.example.kavyasynechrontask.common.fetchimage.cache.ImageCache
import com.example.kavyasynechrontask.common.fetchimage.cache.KeyHash
import com.example.kavyasynechrontask.common.fetchimage.decode.ImageDecoder
import com.example.kavyasynechrontask.common.fetchimage.decode.TempFile
import com.example.kavyasynechrontask.common.fetchimage.strategy.ImageLoadStrategy
import com.example.kavyasynechrontask.common.fetchimage.target.ComputeScale
import com.example.kavyasynechrontask.common.fetchimage.target.GetTargetSize
import com.example.kavyasynechrontask.data.cloud.ConfigCloud
import com.example.kavyasynechrontask.data.cloud.ConfigHttpService
import com.example.kavyasynechrontask.data.cloud.UnmarshallPetListResponse
import com.example.kavyasynechrontask.domain.workinghours.ParseWorkingHours
import com.example.kavyasynechrontask.ui.list.ConfigMapper
import com.example.kavyasynechrontaskapp.navigation.Navigation
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AppContainer(appContext: Context) : DiContainer {

    private val executorService: ExecutorService by lazy {
        Executors.newFixedThreadPool(EXECUTOR_THREADS)
    }

    private fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    private val configHttpService: HttpService<ConfigCloud> by lazy {
        ConfigHttpService(
            CONFIG_URL,
            UnmarshallConfigResponse(),
            provideOkHttpClient()
        )
    }

    private val petHttpService: HttpService<List<PetCloud>> by lazy {
        PetHttpService(
            PETS_URL,
            UnmarshallPetListResponse(),
            provideOkHttpClient()
        )
    }

    override val navigation: Navigation.Component by lazy { Navigation.Base() }

    override val fetchConfigUseCase: UseCase<ConfigDomain> by lazy {
        FetchConfigUseCase(configHttpService, ConfigMapper(ParseWorkingHours.Base()))
    }

    override val fetchPetsUseCase: UseCase<List<PetDomain>> by lazy {
        FetchPetsUseCase(petHttpService, PetListMapper())
    }

    override val checkWorkHours: CheckWorkHours by lazy {
        CheckWorkHours.Base(CurrentDate.Base())
    }

    override val fetchImage: FetchImage by lazy {
        FetchImage.Base(
            placeholder = R.drawable.ic_launcher_foreground,
            loadStrategy = ImageLoadStrategy.Memory(
                memoryCache = ImageCache.MemoryImageCache(),
                next = ImageLoadStrategy.Persistence(
                    persistenceCache = ImageCache.FileImageCache(
                        context = appContext,
                        keyHash = KeyHash.MD5(),
                        imageDecoder = ImageDecoder.FileDecoder()
                    ),
                    executorService = executorService,
                    next = ImageLoadStrategy.Remote(
                        okHttpClient = provideOkHttpClient(),
                        streamImageDecoder = ImageDecoder.StreamDecoder(
                            tempFile = TempFile.Cache(appContext),
                            computeScale = ComputeScale.KeepAspectRatio()
                        )
                    )
                )
            ),
            getTargetSize = GetTargetSize.ViewTarget()
        )
    }

    companion object {
        private const val CONFIG_URL = "https://jsonkeeper.com/b/PN84"
        private const val PETS_URL = "http://localhost:3000/pets"
        private const val EXECUTOR_THREADS = 5
    }
}