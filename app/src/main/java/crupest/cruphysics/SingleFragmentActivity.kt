package crupest.cruphysics

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem

/**
 * An activity with a single page fragment.
 * Navigate fragment using [navigateToFragment].
 * Please override [onCreate] to navigate to the first fragment using [navigateToFragment].
 */
abstract class SingleFragmentActivity : AppCompatActivity(), IOptionMenuActivity {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_fragment)
    }

    final override var optionMenu: Int = 0
        set(value) {
            field = value
            invalidateOptionsMenu()
        }

    final override val optionMenuItemSelectedEvent: ReturnEvent<OptionMenuItemSelectedEventArgs, Boolean> = ReturnEvent()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (optionMenu != 0)
            menuInflater.inflate(optionMenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            optionMenuItemSelectedEvent.raise(OptionMenuItemSelectedEventArgs(item!!)) ?: false

    fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_fragment_container, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        transaction.commit()
    }
}
