package com.example.kavyasynechrontask.ui

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.Atoms.getCurrentUrl
import androidx.test.espresso.web.sugar.Web.onWebView
import com.example.kavyasynechrontask.domain.PetDomain
import com.example.kavyasynechrontask.testing.AppFlowTest
import com.example.kavyasynechrontask.testing.mock.MockDiContainer
import com.example.kavyasynechrontask.testing.viewassertions.RecyclerViewItemCountAssertion
import com.example.kavyasynechrontask.ui.list.PetListFragment
import com.example.kavyasynechrontask.ui.pet.PetFragment
import com.example.kavyasynechrontask.ui.MainActivity
import org.hamcrest.CoreMatchers.*
import org.junit.Assert
import org.junit.Test


class MainActivityTest : AppFlowTest(MainActivity::class, amountIdlingResources = 2) {

    @Test
    fun checkIfActivityIsLaunched() {
        launch {
            onView(withId(com.example.kavyasynechrontask.R.id.container)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun checkIfPetListFragmentPresentedOnStart() {
        launch { scenario ->
            scenario.onActivity { activity ->
                Assert.assertTrue(
                    activity.supportFragmentManager.findFragmentByTag(PetListFragment.TAG) != null
                )
            }
        }
    }

    @Test
    fun shouldConfigAndPetListDisplayed() {
        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.working_hours)).check(matches(isDisplayed()))
            onView(withId(com.example.kavyasynechrontask.R.id.working_hours)).check(
                matches(
                    withSubstring(
                        MockDiContainer.config.mockedConfig.workingHours.origin
                    )
                )
            )
            onView(withId(com.example.kavyasynechrontask.R.id.pet_list)).check(
                RecyclerViewItemCountAssertion(
                    MockDiContainer.config.mockedPetList.size
                )
            )
        }
    }

    @Test
    fun shouldNotShowErrorState() {
        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.errors_layout)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun shouldShowErrorStateOnErrorBothData() {
        updateDiConfig {
            copy(
                isConfigError = true,
                isPetListError = true
            )
        }

        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.errors_layout)).check(matches(isDisplayed()))
            onView(withId(com.example.kavyasynechrontask.R.id.pet_list)).check(RecyclerViewItemCountAssertion(0))
            onView(withId(com.example.kavyasynechrontask.R.id.working_hours)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun shouldShowErrorStateOnErrorConfigData() {
        updateDiConfig {
            copy(
                isConfigError = true
            )
        }

        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.errors_layout)).check(matches(isDisplayed()))
            onView(withId(com.example.kavyasynechrontask.R.id.pet_list)).check(RecyclerViewItemCountAssertion(0))
            onView(withId(com.example.kavyasynechrontask.R.id.working_hours)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun shouldShowErrorOnInvalidPetListData() {
        updateDiConfig {
            copy(
                isPetListError = true
            )
        }

        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.errors_layout)).check(matches(isDisplayed()))
            onView(withId(com.example.kavyasynechrontask.R.id.pet_list)).check(RecyclerViewItemCountAssertion(0))
            onView(withId(com.example.kavyasynechrontask.R.id.working_hours)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun shouldReloadDataOnRetry() {
        updateDiConfig {
            copy(
                isPetListError = true,
                isConfigError = true
            )
        }

        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.errors_layout)).check(matches(isDisplayed()))

            updateDiConfig {
                copy(
                    isPetListError = false,
                    isConfigError = false
                )
            }

            onView(withId(com.example.kavyasynechrontask.R.id.retryBtn)).perform(click())

            onView(withId(com.example.kavyasynechrontask.R.id.errors_layout)).check(matches(not(isDisplayed())))
            onView(withId(com.example.kavyasynechrontask.R.id.working_hours)).check(matches(isDisplayed()))
            onView(withId(com.example.kavyasynechrontask.R.id.working_hours)).check(
                matches(
                    withSubstring(
                        MockDiContainer.config.mockedConfig.workingHours.origin
                    )
                )
            )
            onView(withId(com.example.kavyasynechrontask.R.id.pet_list)).check(
                RecyclerViewItemCountAssertion(
                    MockDiContainer.config.mockedPetList.size
                )
            )
        }
    }

    @Test
    fun shouldOpenPetFragmentOnClickListItem() {
        val expectedPetName = "Pet Name"
        val expectedContentUrl = "https://en.m.wikipedia.org/wiki/Rabbit"

        updateDiConfig {
            copy(
                mockedPetList = listOf(
                    PetDomain(
                        imageUrl = "image",
                        title = expectedPetName,
                        contentUrl = expectedContentUrl
                    )
                )
            )
        }

        launchWithIdling { scenario ->
            onView(withId(com.example.kavyasynechrontask.R.id.pet_list)).check(
                RecyclerViewItemCountAssertion(
                    MockDiContainer.config.mockedPetList.size
                )
            )
            onView(withId(com.example.kavyasynechrontask.R.id.pet_list)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )

            //check pet fragment opened
            scenario.onActivity { activity ->
                Assert.assertTrue(
                    activity.supportFragmentManager.findFragmentByTag(PetFragment.TAG) != null
                )
            }

            // check pet title
            onView(
                allOf(
                    instanceOf(TextView::class.java),
                    withParent(withResourceName("action_bar"))
                )
            ).check(matches(withText(expectedPetName)))

            // WebView is visible
            onView(withId(com.example.kavyasynechrontask.R.id.web_view)).check(matches(isDisplayed()))

            // WebView loaded expected URL
            onWebView()
                .check(webMatches(getCurrentUrl(), containsString(expectedContentUrl)))
        }
    }

    @Test
    fun shouldShowWorkingAlertOnChatClickedWithWorkingHours() {
        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.chat_btn)).check(matches(isDisplayed()))
            onView(withId(com.example.kavyasynechrontask.R.id.chat_btn)).perform(click())

            // check alert
            onView(withText(com.example.kavyasynechrontask.R.string.alert_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))

            onView(withText(com.example.kavyasynechrontask.R.string.alert_working_hours))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun shouldShowNoWorkingAlertOnChatClickedWithNoWorkingHours() {
        updateDiConfig {
            copy(isWorkingHours = false)
        }

        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.chat_btn)).check(matches(isDisplayed()))
            onView(withId(com.example.kavyasynechrontask.R.id.chat_btn)).perform(click())

            // check alert
            onView(withText(com.example.kavyasynechrontask.R.string.alert_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))

            onView(withText(com.example.kavyasynechrontask.R.string.alert_not_working_hours))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun shouldShowWorkingAlertOnCallClickedWithWorkingHours() {
        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.call_btn)).check(matches(isDisplayed()))
            onView(withId(com.example.kavyasynechrontask.R.id.call_btn)).perform(click())

            // check alert
            onView(withText(com.example.kavyasynechrontask.R.string.alert_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))

            onView(withText(com.example.kavyasynechrontask.R.string.alert_working_hours))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun shouldShowNoWorkingAlertOnCallClickedWithNoWorkingHours() {
        updateDiConfig {
            copy(isWorkingHours = false)
        }

        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.call_btn)).check(matches(isDisplayed()))
            onView(withId(com.example.kavyasynechrontask.R.id.call_btn)).perform(click())

            // check alert
            onView(withText(com.example.kavyasynechrontask.R.string.alert_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))

            onView(withText(com.example.kavyasynechrontask.R.string.alert_not_working_hours))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun shouldCallButtonNotDisplayed() {
        updateDiConfig {
            copy(
                mockedConfig = mockedConfig.copy(
                    isCallEnabled = false
                )
            )
        }

        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.call_btn)).check(matches(not(isDisplayed())))
            onView(withId(com.example.kavyasynechrontask.R.id.chat_btn)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun shouldChatButtonNotDisplayed() {
        updateDiConfig {
            copy(
                mockedConfig = mockedConfig.copy(
                    isChatEnabled = false
                )
            )
        }

        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.chat_btn)).check(matches(not(isDisplayed())))
            onView(withId(com.example.kavyasynechrontask.R.id.call_btn)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun shouldChatAndCallButtonNotDisplayed() {
        updateDiConfig {
            copy(
                mockedConfig = mockedConfig.copy(
                    isChatEnabled = false,
                    isCallEnabled = false
                )
            )
        }

        launchWithIdling {
            onView(withId(com.example.kavyasynechrontask.R.id.chat_btn)).check(matches(not(isDisplayed())))
            onView(withId(com.example.kavyasynechrontask.R.id.call_btn)).check(matches(not(isDisplayed())))
        }
    }
}