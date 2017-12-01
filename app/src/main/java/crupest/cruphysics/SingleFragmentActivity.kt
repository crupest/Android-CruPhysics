package crupest.cruphysics

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem

/**
 * An activity with a single page fragment.
 * Navigate fragment using <code>navigateToFragment</code>.
 * Please override <code>onCreate</code> to navigate to the first fragment.
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

    final override val optionMenuItemSelectedEvent: Event<OptionMenuItemSelectedEventArgs> = Event()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (optionMenu != 0)
            menuInflater.inflate(optionMenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        optionMenuItemSelectedEvent.raise(OptionMenuItemSelectedEventArgs(item!!))
        return true
    }

    fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_fragment_container, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        transaction.commit()
    }
}
