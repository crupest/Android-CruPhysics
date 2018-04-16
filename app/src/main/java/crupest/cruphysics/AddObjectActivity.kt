package crupest.cruphysics

import android.content.Intent
import android.os.Bundle
import crupest.cruphysics.fragment.AddObjectListFragment
import crupest.cruphysics.physics.ViewWorld
import crupest.cruphysics.physics.serialization.mapper.map
import crupest.cruphysics.physics.serialization.parseAsJsonObject
import crupest.cruphysics.physics.serialization.toJson
import crupest.cruphysics.physics.serialization.unmapper.unmapViewWorld

class AddObjectActivity : SingleFragmentActivity() {

    companion object {
        const val ARG_WORLD = "WORLD"
        const val RESULT_WORLD = "WORLD"
    }

    lateinit var viewWorld: ViewWorld

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val s = if (savedInstanceState == null) {
            intent.extras.getString(MainActivity.ARG_WORLD)
        } else {
            savedInstanceState.getString(ARG_WORLD)
        }
        viewWorld = unmapViewWorld(s.parseAsJsonObject())

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        navigateToFragment(AddObjectListFragment(), addToBackStack = false)
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState!!.putString(ARG_WORLD, map(viewWorld).toJson())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun setResultAndFinish() {
        val result = Intent()
        result.putExtra(AddObjectActivity.RESULT_WORLD, map(viewWorld).toJson())
        setResult(RESULT_OK, result)
        finish()
    }
}
