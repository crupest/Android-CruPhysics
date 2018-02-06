package crupest.cruphysics

import android.content.Intent
import android.os.Bundle
import crupest.cruphysics.fragment.AddObjectListFragment
import crupest.cruphysics.physics.ViewWorld
import crupest.cruphysics.physics.serialization.JsonParser
import crupest.cruphysics.physics.serialization.mapper.map
import crupest.cruphysics.physics.serialization.unmapper.unmapViewWorld

class AddObjectActivity : SingleFragmentActivity() {

    companion object {
        const val ARG_WORLD = "WORLD"
        const val RESULT_WORLD = "WORLD"
    }

    val json = JsonParser()
    lateinit var viewWorld: ViewWorld

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val s = if (savedInstanceState == null) {
            intent.extras.getString(MainActivity.ARG_WORLD)
        } else {
            savedInstanceState.getString(ARG_WORLD)
        }
        val o = json.objectAdapter.fromJson(s)!!
        viewWorld = unmapViewWorld(o)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        navigateToFragment(AddObjectListFragment(), addToBackStack = false)
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState!!.putString(ARG_WORLD, json.objectAdapter.toJson(map(viewWorld)))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun setResultAndFinish() {
        val result = Intent()
        result.putExtra(AddObjectActivity.RESULT_WORLD, json.objectAdapter.toJson(map(viewWorld)))
        setResult(RESULT_OK, result)
        finish()
    }
}
