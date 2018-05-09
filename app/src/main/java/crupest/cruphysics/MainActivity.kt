package crupest.cruphysics

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import crupest.cruphysics.component.IMainWorldDelegate
import crupest.cruphysics.component.MainWorldCanvas
import crupest.cruphysics.physics.resetWorldViewMatrix
import crupest.cruphysics.physics.serialization.*
import crupest.cruphysics.physics.view.WorldViewData
import crupest.cruphysics.serialization.fromJson
import crupest.cruphysics.serialization.toJson
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.setInterval
import crupest.cruphysics.utility.showAlertDialog
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.World
import org.dyn4j.geometry.Vector2
import java.io.File


/**
 * Activity class [MainActivity].
 * Represents the main activity.
 */
class MainActivity : AppCompatActivity(), IMainWorldDelegate {

    companion object {
        const val WORLD_FILE_NAME = "world.wld"
        const val ARG_WORLD = "WORLD"
        const val ARG_CAMERA = "CAMERA"
        const val ADD_OBJECT_REQUEST_CODE = 2000
    }

    //Region: properties ui

    private lateinit var worldCanvas: MainWorldCanvas

    private var optionMenu: Int = R.menu.main_menu_pause
        set(value) {
            field = value
            invalidateOptionsMenu()
        }


    //Region: properties world

    private lateinit var world: World
    private var task: ScheduleTask? = null

    private lateinit var worldViewData: WorldViewData

    //Region: methods ui

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.tool_bar))

        val floatingButton = findViewById<FloatingActionButton>(R.id.add_floating_button)
        floatingButton.setOnClickListener {
            val intent = Intent(this, AddObjectActivity::class.java)
            intent.putExtra(AddObjectActivity.ARG_WORLD, world.toData().toJson())
            intent.putExtra(AddObjectActivity.ARG_CAMERA, worldCanvas.viewMatrix.toData().toJson())
            startActivityForResult(intent, ADD_OBJECT_REQUEST_CODE)
        }
        worldCanvas = findViewById(R.id.world_canvas)
        worldCanvas.mainWorldDelegate = this

        if (savedInstanceState == null)
            readWorldFromFile()
        else {
            val worldData: WorldData = savedInstanceState.getString(ARG_WORLD).fromJson()
            val cameraData: CameraData = savedInstanceState.getString(ARG_CAMERA).fromJson()

            world = worldData.fromData()
            cameraData.fromData(worldCanvas.viewMatrix)
        }

        createViewData()
    }

    override fun onPause() {
        super.onPause()
        pauseWorld()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveWorldToFile()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState!!.putString(ARG_WORLD, world.toData().toJson())
        outState.putString(ARG_CAMERA, worldCanvas.viewMatrix.toData().toJson())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(optionMenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.play -> {
                runWorld()
                return true
            }
            R.id.pause -> {
                pauseWorld()
                return true
            }
            R.id.create_new -> {
                pauseWorld()
                createNewWorld()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_OBJECT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val bodyData: BodyData = data!!.getStringExtra(AddObjectActivity.RESULT_BODY).fromJson()
            val cameraData: CameraData = data.getStringExtra(AddObjectActivity.RESULT_CAMERA).fromJson()

            addBody(bodyData.fromData())
            cameraData.fromData(worldCanvas.viewMatrix)
            worldCanvas.repaint()
        }
    }

    private fun createViewData() {
        worldViewData = WorldViewData(world.bodies)
        worldCanvas.drawWorldDelegate = worldViewData
    }

    //Region: world

    private fun createNewWorld() {
        world = World()
        worldViewData = WorldViewData()
        worldCanvas.drawWorldDelegate = worldViewData
        worldCanvas.viewMatrix.resetWorldViewMatrix()

        worldCanvas.repaint()
    }

    private fun onWorldStateChanged(newState: Boolean) {
        optionMenu = if (newState) R.menu.main_menu_play else R.menu.main_menu_pause
    }

    private fun runWorld() {
        if (task == null) {
            task = setInterval(1.0 / 60.0) {
                world.update(1.0 / 60.0)
                worldCanvas.repaint()
            }
            onWorldStateChanged(true)
        }
    }

    private fun pauseWorld() {
        if (task != null) {
            task!!.cancel()
            task = null
            onWorldStateChanged(false)
        }
    }

    private fun addBody(body: Body) {
        world.addBody(body)
        worldViewData.registerBody(body)
    }

    override fun removeBody(body: Body) {
        worldViewData.unregisterBody(body)
        world.removeBody(body)
        worldCanvas.repaint()
    }

    override fun bodyHitTest(x: Double, y: Double): Body? =
            world.bodies.firstOrNull { it.contains(Vector2(x, y)) }

    //Region: serialization

    /**
     * Get the file that records the world.
     */
    private fun getWorldFile() = File(filesDir, WORLD_FILE_NAME)

    /**
     * Save the current world state to file([getWorldFile]).
     */
    private fun saveWorldToFile() {
        getWorldFile().writeText(ViewWorldData(
                world = world.toData(), camera = worldCanvas.viewMatrix.toData()).toJson())
    }

    /**
     * Read the world state from the file([getWorldFile]).
     * If the world file doesn't exist or is of bad format,
     * a new world will be created and an alert dialog will be shown.
     */
    private fun readWorldFromFile() {
        val worldFile = getWorldFile()
        if (worldFile.exists()) {
            try {
                val viewWorldData: ViewWorldData = worldFile.readText().fromJson()

                world = viewWorldData.world.fromData()
                worldCanvas.viewMatrix.set(viewWorldData.camera.fromData())

            } catch (exception: Exception) {
                showAlertDialog(this,
                        "World file's format is bad. A new world is created!")
            }
        } else {
            showAlertDialog(this,
                    "World file doesn't exist. A new world is created!")
        }
        createNewWorld()
    }
}
