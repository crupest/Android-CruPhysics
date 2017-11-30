package crupest.cruphysics

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import crupest.cruphysics.fragment.*

class AddObjectActivity : AppCompatActivity(),
        AddObjectListFragment.EventListener,
        IOptionMenuActivity {

    private var addObjectFragment: AddObjectFragment? = null

    override var optionMenu: Int = 0
        set(value) {
            field = value
            invalidateOptionsMenu()
        }

    override val optionMenuItemSelectedEvent: Event<OptionMenuItemSelectedEventArgs> = Event()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_object)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val transaction = supportFragmentManager.beginTransaction()
        val fragment = AddObjectListFragment()
        transaction.add(R.id.main_fragment_container, fragment)
        transaction.commit()
    }

    override fun onAddObjectListItemSelected(position: Int) {
        when (position) {
            0 -> {
                val transaction = supportFragmentManager.beginTransaction()
                val fragment = AddCircleObjectFragment()
                transaction.replace(R.id.main_fragment_container, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
                addObjectFragment = fragment
            }
            1 -> {
                val transaction = supportFragmentManager.beginTransaction()
                val fragment = AddRectangleObjectFragment()
                transaction.replace(R.id.main_fragment_container, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
                addObjectFragment = fragment
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (optionMenu != 0)
            menuInflater.inflate(optionMenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        optionMenuItemSelectedEvent.raise(OptionMenuItemSelectedEventArgs(item!!))
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
