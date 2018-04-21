package crupest.cruphysics

import android.app.Activity
import android.content.Intent
import android.graphics.Matrix
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import crupest.cruphysics.component.WorldCanvas
import crupest.cruphysics.physics.serialization.ViewWorldData
import crupest.cruphysics.physics.serialization.toData
import crupest.cruphysics.serialization.fromJson
import crupest.cruphysics.serialization.toJson
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.setInterval
import org.dyn4j.dynamics.World
import java.io.File


/**
 * Activity class [MainActivity].
 * Represents the main activity.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        const val WORLD_FILE_NAME = "world.wld"
        const val ARG_WORLD = "WORLD"
        const val ARG_GRAVITY = "GRAVITY"
        const val ADD_OBJECT_REQUEST_CODE = 2000
        const val SETTINGS_REQUEST_CODE = 2001
    }

    //Region: properties ui

    private lateinit var worldCanvas: WorldCanvas

    private var optionMenu: Int = R.menu.main_menu_pause
        set(value) {
            field = value
            invalidateOptionsMenu()
        }



    //Region: properties world

    private var world: World = World()
    private var viewMatrix: Matrix = Matrix()
    private var task: ScheduleTask? = null



    //Region: properties ui

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.tool_bar))

        val floatingButton = findViewById<FloatingActionButton>(R.id.add_floating_button)
        floatingButton.setOnClickListener {
            val intent = Intent(this, AddObjectActivity::class.java)
            intent.putExtra(ARG_WORLD, map(viewWorld).toJson())
            startActivityForResult(intent, ADD_OBJECT_REQUEST_CODE)
        }
        worldCanvas = findViewById(R.id.world_canvas)

    }

    override fun onPause() {
        super.onPause()
        pauseWorld()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveWorldToFile()
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
                TODO()
                return true
            }
            R.id.settings -> {
                val intent = Intent(this, WorldSettingsActivity::class.java)
                intent.putExtra(ARG_GRAVITY, viewWorld.world.gravity.toJson())
                startActivityForResult(intent, SETTINGS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_OBJECT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val s = data!!.getStringExtra(AddObjectActivity.RESULT_WORLD)
            TODO()
        } else if (requestCode == SETTINGS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val s = data!!.getStringExtra(WorldSettingsActivity.RESULT_GRAVITY)
            TODO()
        }
    }


    //Region: world

    private fun createNewWorld() {
        TODO()
    }

    private fun onWorldStateChanged(newState: Boolean) {
        optionMenu = if (newState) R.menu.main_menu_play else R.menu.main_menu_pause
    }

    private fun onWorldStepped() {
        worldCanvas.invalidate()
    }

    private fun runWorld() {
        if (task == null) {
            task = setInterval(1.0 / 60.0) {
                world.update(1.0 / 60.0)
                runOnUiThread {
                    onWorldStepped()
                }
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


    //Region: serialization

    /**
     * Get the file that records the world.
     */
    private fun getWorldFile() = File(filesDir, WORLD_FILE_NAME)

    /**
     * Save the current world state to file([getWorldFile]).
     */
    private fun saveWorldToFile() {
        getWorldFile().writeText(
                ViewWorldData(world = world.toData(), camera = viewMatrix.toData()).toJson()
        )
    }

    /**
     * Read the world state from the file([getWorldFile]).
     * Return true if the world is read successfully.
     * Return false if the file doesn't exists.
     * Throw an exception if the file has a bad format.
     */
    private fun readWorldFromFile(): Boolean {
        val worldFile = getWorldFile()
        if (worldFile.exists()) {
            val viewWorldData: ViewWorldData = worldFile.readText().fromJson()!!
            TODO()
        }
    }
}
