package crupest.cruphysics.test

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.UiController
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
import org.hamcrest.Matchers.*
import androidx.test.espresso.ViewAction
import org.hamcrest.Matcher
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.PerformException
import androidx.test.espresso.util.TreeIterables
import java.util.concurrent.TimeoutException
import androidx.test.espresso.matcher.ViewMatchers.isRoot


fun waitFor(millis: Long) {
    onView(isRoot()).perform(object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isRoot()
        }

        override fun getDescription(): String {
            return "Wait for $millis milliseconds."
        }

        override fun perform(uiController: UiController, view: View) {
            uiController.loopMainThreadForAtLeast(millis)
        }
    })
}

fun waitId(viewId: Int, millis: Long) {
    onView(isRoot()).perform(object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isRoot()
        }

        override fun getDescription(): String {
            return "wait for a specific view with id <$viewId> during $millis millis."
        }

        override fun perform(uiController: UiController, view: View) {
            uiController.loopMainThreadUntilIdle()
            val startTime = System.currentTimeMillis()
            val endTime = startTime + millis
            val viewMatcher = withId(viewId)

            do {
                for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                    // found view with required ID
                    if (viewMatcher.matches(child)) {
                        return
                    }
                }

                uiController.loopMainThreadForAtLeast(50)
            } while (System.currentTimeMillis() < endTime)

            // timeout happens
            throw PerformException.Builder()
                    .withActionDescription(this.description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(TimeoutException())
                    .build()
        }
    })
}


@RunWith(AndroidJUnit4::class)
class ActivityUnitTest {
    @get:Rule
    val rule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun test() {
        onView(withId(R.id.add_floating_button)).perform(click())
        onView(withId(R.id.next)).perform(click())
        onView(withId(R.id.next)).perform(click())

        onData(allOf(instanceOf(String::class.java), equalTo("dynamic")))
                .inAdapterView(withId(R.id.body_type_spinner)).perform(click())
        onView(withId(R.id.edit_density)).perform(replaceText("3"))

        Espresso.pressBack()
        onView(withId(R.id.next)).perform(click())

        onView(withId(R.id.body_type_spinner)).check(matches(withSpinnerText(containsString("dynamic"))))
        onView(withId(R.id.edit_density)).check(matches(withText("3")))
    }
}
