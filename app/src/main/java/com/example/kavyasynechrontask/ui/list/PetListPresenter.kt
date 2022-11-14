package com.example.kavyasynechrontask.ui.list

import com.example.kavyasynechrontask.common.ui.Presenter
import com.example.kavyasynechrontask.common.ui.UseCase
import com.example.kavyasynechrontask.ui.list.PetListAlert
import com.example.kavyasynechrontask.R
import com.example.kavyasynechrontaskapp.common.async.CombinedCallback
import com.example.kavyasynechrontaskapp.common.observer.SingleEventLiveData
import com.example.kavyasynechrontaskapp.common.observer.SingleStateLiveData
import com.example.kavyasynechrontaskapp.common.ui.UiExecutor
import com.example.kavyasynechrontaskapp.domain.ConfigDomain
import com.example.kavyasynechrontaskapp.domain.PetDomain
import com.example.kavyasynechrontaskapp.domain.workinghours.CheckWorkHours
import com.example.kavyasynechrontaskapp.navigation.Navigation
import com.example.kavyasynechrontaskapp.navigation.Screen

class PetListPresenter(
    private val navigation: Navigation.Route,
    private val checkWorkHours: CheckWorkHours,
    private val fetchConfigUseCase: UseCase<ConfigDomain>,
    private val fetchPetsUseCase: UseCase<List<PetDomain>>,
    uiExecutor: UiExecutor = UiExecutor.Main()
) : Presenter {

    private val _configState = SingleStateLiveData(ConfigDomain.EMPTY, uiExecutor)
    val configState get() = _configState.asDataObserver()

    private val _listState = SingleStateLiveData(emptyList<PetDomain>(), uiExecutor)
    val listState get() = _listState.asDataObserver()

    private val _showAlert = SingleEventLiveData<PetListAlert>(uiExecutor)
    val showAlert get() = _showAlert.asDataObserver()

    private val _loadingState = SingleStateLiveData(false, uiExecutor)
    val loadingState get() = _loadingState.asDataObserver()

    private val _errorState = SingleStateLiveData(false, uiExecutor)
    val errorState get() = _errorState.asDataObserver()

    private val _errors = SingleEventLiveData<Throwable>(uiExecutor)
    val errors get() = _errors.asDataObserver()

    init {
        fetch()
    }

    private fun fetch() {
        _loadingState.emit(true)

        CombinedCallback(
            fetchConfigUseCase::invoke,
            fetchPetsUseCase::invoke
        ).invoke(onResult = { (config, list) ->
            _configState.emit(config)
            _listState.emit(list)

            _loadingState.emit(false)
        }, onError = { error ->
            _errors.emit(error)
            _errorState.emit(true)

            _loadingState.emit(false)
        })
    }

    fun retry() {
        _errorState.emit(false)
        fetch()
    }

    fun routeToPet(petUri: String, petTitle: String) {
        navigation.to(Screen.Pet(petUri, petTitle))
    }

    fun chat() = checkHours()

    fun call() = checkHours()

    private fun checkHours() {
        val isWorking = checkWorkHours.check(_configState.value.workingHours)
        _showAlert.emit(
            PetListAlert(
                R.string.alert_title,
                if (isWorking)
                    R.string.alert_working_hours
                else
                    R.string.alert_not_working_hours
            )
        )
    }
}