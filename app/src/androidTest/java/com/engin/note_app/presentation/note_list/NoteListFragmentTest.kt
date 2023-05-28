package com.engin.note_app.presentation.note_list

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.engin.note_app.R
import com.engin.note_app.launchFragmentInHiltContainer
import com.engin.note_app.presentation.note_list.adapter.NoteListRecyclerAdapter
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class NoteListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val NOTE_POSITION = 2

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun navigate_add_note_screen(){
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<NoteListFragment> {
            this.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                if (viewLifecycleOwner != null) {
                    // The fragment’s view has just been created
                    navController.setGraph(R.navigation.main_flow)
                    Navigation.setViewNavController(this.requireView(), navController)
                }
            }
        }
        onView(withId(R.id.add_note_fab)).perform(click())
        Truth.assertThat(navController.currentDestination?.id).isEqualTo(R.id.noteEditFragment)
    }

    @Test
    fun is_fragment_launch_and_visible_recyclerview(){
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<NoteListFragment> {
            this.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                if (viewLifecycleOwner != null) {
                    // The fragment’s view has just been created
                    navController.setGraph(R.navigation.main_flow)
                    Navigation.setViewNavController(this.requireView(), navController)
                }
            }
        }
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun click_note_and_display_detail(){
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<NoteListFragment> {
            this.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                if (viewLifecycleOwner != null) {
                    // The fragment’s view has just been created
                    navController.setGraph(R.navigation.main_flow)
                    Navigation.setViewNavController(this.requireView(), navController)
                }
            }
        }
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition<NoteListRecyclerAdapter.NoteListViewHolder>(NOTE_POSITION, click()))
        Truth.assertThat(navController.currentDestination?.id).isEqualTo(R.id.noteEditFragment)
    }
}