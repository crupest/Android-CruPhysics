package crupest.cruphysics

import android.content.Intent
import android.os.Bundle
import crupest.cruphysics.fragment.AddObjectListFragment
import crupest.cruphysics.physics.serialization.BodyData
import crupest.cruphysics.physics.serialization.CameraData
import crupest.cruphysics.physics.serialization.WorldData
import crupest.cruphysics.physics.view.StaticWorldViewData
import crupest.cruphysics.serialization.fromJson
import crupest.cruphysics.serialization.toJson

class AddObjectActivity : SingleFragmentActivity() {

    companion object {
        const val ARG_WORLD = "WORLD"
        const val ARG_CAMERA = "CAMERA"
        const val RESULT_BODY = "BODY"
        const val RESULT_CAMERA = "CAMERA"
    }

    lateinit var worldData: WorldData
    lateinit var worldViewData: StaticWorldViewData
    lateinit var cameraData: CameraData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val worldString: String
        val cameraString: String
        if (savedInstanceState == null) {
            worldString = intent.extras.getString(ARG_WORLD)
            cameraString = intent.extras.getString(ARG_CAMERA)
        } else {
            worldString = savedInstanceState.getString(ARG_WORLD)
            cameraString = savedInstanceState.getString(ARG_CAMERA)
        }

        cameraData = cameraString.fromJson()

        worldData = worldString.fromJson()
        worldViewData = StaticWorldViewData(worldData)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        navigateToFragment(AddObjectListFragment(), addToBackStack = false)
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState!!.putString(ARG_WORLD, worldData.toJson())
        outState.putString(ARG_CAMERA, cameraData.toJson())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun setResultAndFinish(bodyData: BodyData, camera: CameraData) {
        val result = Intent()
        result.putExtra(AddObjectActivity.RESULT_BODY, bodyData.toJson())
        result.putExtra(AddObjectActivity.RESULT_CAMERA, camera.toJson())
        setResult(RESULT_OK, result)
        finish()
    }
}
