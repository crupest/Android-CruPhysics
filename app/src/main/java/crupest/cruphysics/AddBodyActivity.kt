package crupest.cruphysics

import android.content.Intent
import android.os.Bundle
import crupest.cruphysics.fragment.AddBodyShapeListFragment
import crupest.cruphysics.physics.serialization.*
import crupest.cruphysics.physics.view.StaticWorldViewData
import crupest.cruphysics.serialization.fromJson
import crupest.cruphysics.serialization.toJson
import crupest.cruphysics.utility.generateRandomColor


class AddBodyActivity : SingleFragmentActivity() {

    companion object {
        const val ARG_WORLD = "WORLD"
        const val ARG_CAMERA = "CAMERA"
        const val ARG_BODY = "ARG_BODY"
        const val RESULT_BODY = "BODY"
        const val RESULT_CAMERA = "CAMERA"
    }

    private lateinit var worldData: WorldData
    lateinit var worldViewData: StaticWorldViewData
    lateinit var cameraData: CameraData
    lateinit var resultBodyData: BodyData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val worldString: String
        val cameraString: String
        if (savedInstanceState == null) {
            worldString = intent.extras.getString(ARG_WORLD)
            cameraString = intent.extras.getString(ARG_CAMERA)

            resultBodyData = createInitBodyData()

            navigateToFragment(AddBodyShapeListFragment(), addToBackStack = false)
        } else {
            worldString = savedInstanceState.getString(ARG_WORLD)
            cameraString = savedInstanceState.getString(ARG_CAMERA)

            resultBodyData = savedInstanceState.getString(ARG_BODY).fromJson()
        }

        cameraData = cameraString.fromJson()

        worldData = worldString.fromJson()
        worldViewData = StaticWorldViewData(worldData)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState!!.putString(ARG_WORLD, worldData.toJson())
        outState.putString(ARG_CAMERA, cameraData.toJson())
        outState.putString(ARG_BODY, resultBodyData.toJson())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun createInitBodyData(): BodyData = BodyData(
            type = BODY_TYPE_STATIC,
            density = 1.0,
            restitution = 0.0,
            friction = 0.2,
            appearance = BodyAppearanceData(color = generateRandomColor())
    )

    fun setResultAndFinish() {
        val result = Intent()
        result.putExtra(AddBodyActivity.RESULT_BODY, resultBodyData.toJson())
        result.putExtra(AddBodyActivity.RESULT_CAMERA, cameraData.toJson())
        setResult(RESULT_OK, result)
        finish()
    }
}
