package crupest.cruphysics

import android.os.Bundle
import crupest.cruphysics.fragment.AddObjectListFragment

class AddObjectActivity : SingleFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        navigateToFragment(AddObjectListFragment(), addToBackStack = false)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
