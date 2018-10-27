package crupest.cruphysics.test

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import crupest.cruphysics.MainActivity
import crupest.cruphysics.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivityUnitTest {
    @get:Rule val rule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun test() {
        onView(withId(R.id.add_floating_button)).perform(click())
    }
}
