package crupest.cruphysics

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * An activity with a single page fragment.
 * Navigate fragment using [navigateToFragment].
 * Please override [onCreate] to navigate to the first fragment using [navigateToFragment].
 */
abstract class SingleFragmentActivity : AppCompatActivity(), IOptionMenuActivity {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_fragment)
        setSupportActionBar(findViewById(R.id.tool_bar))
    }

    final override var optionMenu: Int = 0
        set(value) {
            field = value
            invalidateOptionsMenu()
        }

    final override var optionMenuItemSelectedListener: ((MenuItem) -> Boolean)? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (optionMenu != 0)
            menuInflater.inflate(optionMenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            if (optionMenuItemSelectedListener != null)
                optionMenuItemSelectedListener!!(item!!)
            else
                super.onOptionsItemSelected(item)

    fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_fragment_container, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        transaction.commit()
    }
}
