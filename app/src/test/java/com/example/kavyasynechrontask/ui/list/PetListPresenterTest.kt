package com.example.kavyasynechrontask.ui.list

import com.example.kavyasynechrontask.common.ui.UiExecutor
import com.example.kavyasynechrontask.common.ui.UseCase
import com.example.kavyasynechrontask.domain.ConfigDomain
import com.example.kavyasynechrontask.domain.PetDomain
import com.example.kavyasynechrontask.domain.date.*
import com.example.kavyasynechrontask.domain.workinghours.CheckWorkHours
import com.example.kavyasynechrontask.domain.workinghours.CurrentDate
import com.example.kavyasynechrontask.navigation.Navigation
import com.example.kavyasynechrontask.navigation.Screen
import com.example.kavyasynechrontask.testing.getOrAwaitValue
import com.example.kavyasynechrontask.ui.list.PetListPresenter
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.TimeoutException

class PetListPresenterTest {

    private val navigation = Navigation.Base(UiExecutor.Test())
    private val checkWorkHours = CheckWorkHours.Base(object : CurrentDate {
        override fun get(): DateDomain {
            return DateDomain(2, TimeDomain(15, 0))
        }
    })
    private val checkNotWorkingHours = CheckWorkHours.Base(object : CurrentDate {
        override fun get(): DateDomain {
            return DateDomain(1, TimeDomain(15, 0))
        }
    })
    private val successConfigUseCase = object : UseCase<ConfigDomain> {
        override fun invoke(
            onResult: (ConfigDomain) -> Unit,
            onError: (Throwable) -> Unit
        ) {
            onResult.invoke(expectedConfigDomain)
        }
    }
    private val successPetListUseCase = object : UseCase<List<PetDomain>> {
        override fun invoke(
            onResult: (List<PetDomain>) -> Unit,
            onError: (Throwable) -> Unit
        ) {
            onResult.invoke(expectedPetDomain)
        }
    }
    private val errorConfigUseCase = object : UseCase<ConfigDomain> {
        override fun invoke(
            onResult: (ConfigDomain) -> Unit,
            onError: (Throwable) -> Unit
        ) {
            onError.invoke(RuntimeException())
        }
    }
    private val errorPetListUseCase = object : UseCase<List<PetDomain>> {
        override fun invoke(
            onResult: (List<PetDomain>) -> Unit,
            onError: (Throwable) -> Unit
        ) {
            onError.invoke(RuntimeException())
        }
    }

    private val expectedConfigDomain = ConfigDomain(
        isChatEnabled = true,
        isCallEnabled = true,
        workingHours = WorkHoursDomain(
            "M-F 9:00 - 18:00",
            DateRangeDomain(
                DayDomain.M, DayDomain.F,
                TimeDomain(9, 0),
                TimeDomain(18, 0)
            )
        )
    )
    private val expectedPetDomain = listOf(
        PetDomain(
            imageUrl = "https://upload.wikimedia.org/wikipedia" +
                    "/commons/thumb/0/0b/Cat_poster_1.jpg/" +
                    "1200px-Cat_poster_1.jpg",
            title = "Cat",
            contentUrl = "https://en.wikipedia.org/wiki/Cat"
        )
    )

    @Test
    fun `data presented on success data`() {
        val presenter = PetListPresenter(
            navigation,
            checkWorkHours,
            successConfigUseCase,
            successPetListUseCase,
        )

        Assert.assertEquals(
            expectedPetDomain,
            presenter.listState.getOrAwaitValue()
        )
        Assert.assertEquals(
            expectedConfigDomain,
            presenter.configState.getOrAwaitValue()
        )
    }

    @Test
    fun `empty data and error presented on error data`() {
        val presenter = PetListPresenter(
            navigation,
            checkWorkHours,
            errorConfigUseCase,
            errorPetListUseCase
        )

        Assert.assertEquals(
            emptyList<PetDomain>(),
            presenter.listState.getOrAwaitValue()
        )
        Assert.assertEquals(
            ConfigDomain.EMPTY,
            presenter.configState.getOrAwaitValue()
        )
        Assert.assertTrue(presenter.errors.getOrAwaitValue() is java.lang.RuntimeException)
        Assert.assertTrue(presenter.errorState.getOrAwaitValue())
    }

    @Test
    fun `should retry success data loading after failed`() {

        var errorUseCase = true

        val errorConfigUseCase = object : UseCase<ConfigDomain> {
            override fun invoke(
                onResult: (ConfigDomain) -> Unit,
                onError: (Throwable) -> Unit
            ) {
                if (errorUseCase) {
                    onError.invoke(RuntimeException())
                } else {
                    onResult.invoke(expectedConfigDomain)
                }
            }
        }
        val errorPetListUseCase = object : UseCase<List<PetDomain>> {
            override fun invoke(
                onResult: (List<PetDomain>) -> Unit,
                onError: (Throwable) -> Unit
            ) {
                if (errorUseCase) {
                    onError.invoke(RuntimeException())
                } else {
                    onResult.invoke(expectedPetDomain)
                }
            }
        }

        val presenter = PetListPresenter(
            navigation,
            checkWorkHours,
            errorConfigUseCase,
            errorPetListUseCase
        )

        Assert.assertEquals(
            emptyList<PetDomain>(),
            presenter.listState.getOrAwaitValue()
        )
        Assert.assertEquals(
            ConfigDomain.EMPTY,
            presenter.configState.getOrAwaitValue()
        )
        Assert.assertTrue(presenter.errors.getOrAwaitValue() is java.lang.RuntimeException)
        Assert.assertTrue(presenter.errorState.getOrAwaitValue())

        errorUseCase = false
        presenter.retry()

        Assert.assertEquals(
            expectedPetDomain,
            presenter.listState.getOrAwaitValue()
        )
        Assert.assertEquals(
            expectedConfigDomain,
            presenter.configState.getOrAwaitValue()
        )
        Assert.assertThrows(TimeoutException::class.java) {
            presenter.errors.getOrAwaitValue()
        }
        Assert.assertFalse(presenter.errorState.getOrAwaitValue())
    }

    @Test
    fun `should show any alert on call and chat`() {
        val presenter = PetListPresenter(
            navigation,
            checkWorkHours,
            successConfigUseCase,
            successPetListUseCase,
        )

        presenter.call()
        presenter.showAlert.getOrAwaitValue()

        presenter.showAlert.removeObserver()

        presenter.chat()
        presenter.showAlert.getOrAwaitValue()
    }

    @Test
    fun `should show working alert on working data`() {
        val presenter = PetListPresenter(
            navigation,
            checkWorkHours,
            successConfigUseCase,
            successPetListUseCase,
        )

        presenter.call()

        var alert = presenter.showAlert.getOrAwaitValue()
        Assert.assertTrue(alert.message == com.example.kavyasynechrontaskapp.R.string.alert_working_hours)

        presenter.showAlert.removeObserver()

        presenter.chat()
        alert = presenter.showAlert.getOrAwaitValue()
        Assert.assertTrue(alert.message == com.example.kavyasynechrontaskapp.R.string.alert_working_hours)
    }

    @Test
    fun `should show not working alert on not working data`() {
        val presenter = PetListPresenter(
            navigation,
            checkNotWorkingHours,
            successConfigUseCase,
            successPetListUseCase,
        )

        presenter.call()
        var alert = presenter.showAlert.getOrAwaitValue()
        Assert.assertTrue(alert.message == com.example.kavyasynechrontaskapp.R.string.alert_not_working_hours)

        presenter.showAlert.removeObserver()

        presenter.chat()
        alert = presenter.showAlert.getOrAwaitValue()
        Assert.assertTrue(alert.message == com.example.kavyasynechrontaskapp.R.string.alert_not_working_hours)
    }

    @Test
    fun `should route to pet screen`() {
        val presenter = PetListPresenter(
            navigation,
            checkNotWorkingHours,
            successConfigUseCase,
            successPetListUseCase,
        )
        val petTitle = "TestTitle"
        val petUri = "TestUri"
        val expectedPetScreen = Screen.Pet(petUri, petTitle)

        presenter.routeToPet(petUri, petTitle)

        val actual = navigation.observe().getOrAwaitValue()

        Assert.assertEquals(expectedPetScreen, actual)
    }
}