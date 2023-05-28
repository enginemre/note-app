package com.engin.note_app.presentation.note_add_edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.MediumTest
import com.engin.note_app.MainActivity
import com.engin.note_app.R
import com.engin.note_app.launchFragmentInHiltContainer
import com.engin.note_app.util.forceClick
import com.engin.note_app.util.waitId
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class NoteAddEditFragmentTest{

    @get:Rule
    var instantTaskExecutorRule =  InstantTaskExecutorRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun create_new_note(){
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        val bundle = bundleOf().apply {
            putLong("note_id",0L)
        }
        launchFragmentInHiltContainer<NoteAddEditFragment> {
            this.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                if (viewLifecycleOwner != null) {
                    // The fragment’s view has just been created
                    navController.setGraph(R.navigation.main_flow)
                    navController.setCurrentDestination(R.id.noteEditFragment,bundle)
                    Navigation.setViewNavController(this.requireView(), navController)
                }
            }
        }
        onView(isRoot()).perform(waitId(R.id.note_title_et, TimeUnit.SECONDS.toMillis(5)))
        onView(withId(R.id.note_title_et)).perform(typeText("Deneme 3"))
        onView(withId(R.id.note_description_et)).perform(typeText("Selam Bu Otomasyondan gelen mesaj"))
        onView(withId(R.id.note_image_et)).perform(typeText("https://i4.hurimg.com/i/hurriyet/75/750x422/645a70534e3fe02aac4b4177.jpg"))
        closeSoftKeyboard()
        onView(withId(R.id.confirm_button)).perform(forceClick())
    }


}