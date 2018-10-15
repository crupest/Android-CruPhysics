package crupest.cruphysics

import android.content.Intent
import android.os.Bundle
import crupest.cruphysics.fragment.AddBodyShapeListFragment
import crupest.cruphysics.serialization.data.*
import crupest.cruphysics.serialization.fromJson
import crupest.cruphysics.serialization.toJson


class AddBodyActivity : SingleFragmentActivity() {

    companion object {
        const val ARG_WORLD = "WORLD"
        const val ARG_CAMERA = "CAMERA"
        private const val ARG_SHAPE_TYPE = "ARG_SHAPE_TYPE"
        private const val ARG_SHAPE_INFO_MAP = "ARG_SHAPE_INFO_MAP"
        private const val ARG_BODY_PROPERTY = "ARG_BODY_PROPERTY"
        const val RESULT_BODY = "BODY"
        const val RESULT_CAMERA = "CAMERA"
    }

    private lateinit var worldData: WorldData
    lateinit var worldViewData: StaticWorldViewData
    lateinit var cameraData: CameraData

    lateinit var shapeType: String
    lateinit var shapeInfoMap: MutableMap<String, ShapeInfo>
    lateinit var bodyProperty: BodyProperty

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val worldString: String
        val cameraString: String
        if (savedInstanceState == null) {
            worldString = intent.extras!!.getString(ARG_WORLD)!!
            cameraString = intent.extras!!.getString(ARG_CAMERA)!!

            shapeType = SHAPE_TYPE_CIRCLE
            shapeInfoMap = mutableMapOf()
            bodyProperty = BodyProperty()

            navigateToFragment(AddBodyShapeListFragment(), addToBackStack = false)
        } else {
            worldString = savedInstanceState.getString(ARG_WORLD)!!
            cameraString = savedInstanceState.getString(ARG_CAMERA)!!
            shapeType = savedInstanceState.getString(ARG_SHAPE_TYPE)!!
            shapeInfoMap = savedInstanceState.getString(ARG_SHAPE_INFO_MAP)!!.fromJson()
            bodyProperty = savedInstanceState.getString(ARG_BODY_PROPERTY)!!.fromJson()
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
        outState.putString(ARG_SHAPE_TYPE, shapeType)
        outState.putString(ARG_SHAPE_INFO_MAP, shapeInfoMap.toJson())
        outState.putString(ARG_BODY_PROPERTY, bodyProperty.toJson())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun setResultAndFinish() {
        val result = Intent()
        val shapeInfo = shapeInfoMap[shapeType] ?: throw IllegalStateException("Invalid shape type.")
        result.putExtra(AddBodyActivity.RESULT_BODY, BodyData(
                shape = shapeInfo.shapeData,
                type = bodyProperty.type,
                position = shapeInfo.position,
                rotation = shapeInfo.rotation,
                density = bodyProperty.density,
                restitution = bodyProperty.restitution,
                friction = bodyProperty.friction,
                appearance = BodyAppearanceData(bodyProperty.color)
        ).toJson())
        result.putExtra(AddBodyActivity.RESULT_CAMERA, cameraData.toJson())
        setResult(RESULT_OK, result)
        finish()
    }
}
