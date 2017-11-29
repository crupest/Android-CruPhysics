package crupest.cruphysics

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import crupest.cruphysics.fragment.*

class AddObjectActivity : AppCompatActivity(),
        AddObjectListFragment.EventListener,
        AddObjectFragment.EventListener {

    private var addObjectFragment: AddObjectFragment? = null

    private var optionMenu: Int = 0
        set(value) {
            field = value
            invalidateOptionsMenu()
        }

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

    @SuppressLint("CommitTransaction")
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
        optionMenu = R.menu.add_object_menu
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (optionMenu != 0)
            menuInflater.inflate(optionMenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.ok) {
            addObjectFragment!!.onOk()
            return true
        }
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onAddObjectFragmentDetach() {
        optionMenu = 0
    }
}
