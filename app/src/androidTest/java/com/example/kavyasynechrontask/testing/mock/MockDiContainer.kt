package com.example.kavyasynechrontask.testing.mock

import android.widget.ImageView
import com.example.kavyasynechrontask.common.fetchimage.FetchImage
import com.example.kavyasynechrontask.common.ui.UseCase
import com.example.kavyasynechrontask.di.DiContainer
import com.example.kavyasynechrontask.domain.ConfigDomain
import com.example.kavyasynechrontask.domain.PetDomain
import com.example.kavyasynechrontask.domain.date.WorkHoursDomain
import com.example.kavyasynechrontask.domain.workinghours.CheckWorkHours
import com.example.kavyasynechrontask.navigation.Navigation
import com.example.kavyasynechrontask.testing.idling.EspressoIdlingResource

object MockDiContainer : DiContainer {

    var config = DiContainerConfig()

    override val navigation: Navigation.Component = Navigation.Base()

    override val fetchConfigUseCase: UseCase<ConfigDomain> = object : UseCase<ConfigDomain> {
        override fun invoke(onResult: (ConfigDomain) -> Unit, onError: (Throwable) -> Unit) {
            if (!config.isConfigError) {
                onResult.invoke(config.mockedConfig)
            } else {
                onError.invoke(MockedException())
            }
            EspressoIdlingResource.safeDecrement()
        }
    }

    override val fetchPetsUseCase: UseCase<List<PetDomain>> = object : UseCase<List<PetDomain>> {
        override fun invoke(onResult: (List<PetDomain>) -> Unit, onError: (Throwable) -> Unit) {
            if (!config.isPetListError) {
                onResult.invoke(config.mockedPetList)
            } else {
                onError.invoke(MockedException())
            }
            EspressoIdlingResource.safeDecrement()
        }
    }

    override val checkWorkHours: CheckWorkHours = object : CheckWorkHours {
        override fun check(workHours: WorkHoursDomain): Boolean {
            return config.isWorkingHours
        }
    }

    override val fetchImage: FetchImage = object : FetchImage {
        override fun into(view: ImageView, url: String, onError: (e: Throwable) -> Unit) = Unit
        override fun cancel(view: ImageView) = Unit
    }
}