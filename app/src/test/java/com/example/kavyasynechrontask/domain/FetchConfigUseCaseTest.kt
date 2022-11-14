package com.example.kavyasynechrontask.domain

import com.example.kavyasynechrontask.data.cloud.ConfigHttpService
import com.example.kavyasynechrontask.data.cloud.UnmarshallConfigResponse
import com.example.kavyasynechrontask.data.cloud.exceptions.InvalidConfigDataException
import com.example.kavyasynechrontask.domain.date.DateRangeDomain
import com.example.kavyasynechrontask.domain.date.DayDomain
import com.example.kavyasynechrontask.domain.date.TimeDomain
import com.example.kavyasynechrontask.domain.date.WorkHoursDomain
import com.example.kavyasynechrontask.domain.workinghours.ParseWorkingHours
import com.example.kavyasynechrontask.testing.mockOkHttpClient
import com.example.kavyasynechrontask.ui.list.ConfigMapper
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class FetchConfigUseCaseTest {
    private val mockUrl = "https://test/config"

    @Test
    fun testConfigUseCase() {

        val useCase = FetchConfigUseCase(
            ConfigHttpService(
                url = mockUrl,
                unmarshallResponse = UnmarshallConfigResponse(),
                client = mockOkHttpClient(
                    mockUrl,
                    "{\"settings\":{\"isChatEnabled\":true,\"isCallEnabled\":true," +
                            "\"workHours\":\"M-F 9:00 - 18:00\"}}"
                )
            ),
            ConfigMapper(ParseWorkingHours.Base())
        )

        val expected = ConfigDomain(
            isChatEnabled = true,
            isCallEnabled = true,
            workingHours = WorkHoursDomain(
                "M-F 9:00 - 18:00",
                DateRangeDomain(
                    DayDomain.M, DayDomain.F,
                    TimeDomain(9, 0), TimeDomain(18, 0)
                )
            )
        )
        var data: ConfigDomain = ConfigDomain.EMPTY

        val lock = CountDownLatch(1)
        useCase.invoke({
            data = it
            lock.countDown()
        }, {
            Assert.fail()
        })

        lock.await(2000, TimeUnit.MILLISECONDS)

        Assert.assertEquals(expected, data)
    }

    @Test
    fun testFailedConfigUseCase() {

        val useCase = FetchConfigUseCase(
            ConfigHttpService(
                url = mockUrl,
                unmarshallResponse = UnmarshallConfigResponse(),
                client = mockOkHttpClient(
                    mockUrl,
                    "failed"
                )
            ),
            ConfigMapper(ParseWorkingHours.Base())
        )
        var error: Throwable = RuntimeException()
        val lock = CountDownLatch(1)
        useCase.invoke({
            Assert.fail()
        }, {
            error = it
            lock.countDown()
        })

        lock.await(2000, TimeUnit.MILLISECONDS)

        Assert.assertTrue(error is InvalidConfigDataException)
    }
}