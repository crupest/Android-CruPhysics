package crupest.cruphysics

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import crupest.cruphysics.component.WorldCanvas
import crupest.cruphysics.physics.ViewWorld
import crupest.cruphysics.physics.serialization.JsonParser
import crupest.cruphysics.physics.serialization.mapper.map
import crupest.cruphysics.physics.serialization.unmapper.unmapViewWorld
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.setInterval
import org.dyn4j.dynamics.Step
import org.dyn4j.dynamics.StepAdapter
import org.dyn4j.dynamics.World
import org.dyn4j.geometry.Vector2
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        const val WORLD_FILE_NAME = "world.wld"
        const val ARG_WORLD = "WORLD"
        const val ARG_GRAVITY = "GRAVITY"
        const val ADD_OBJECT_REQUEST_CODE = 2000
        const val SETTINGS_REQUEST_CODE = 2001
    }

    private val json = JsonParser()
    private lateinit var worldCanvas: WorldCanvas
    private var viewWorld: ViewWorld = ViewWorld()
        set(value) {
            val oldWorld = field
            field = value
            onWorldChanged(oldWorld, value)
        }

    private var noUpdateWorld = false
    private var newGravity: Vector2? = null

    private var optionMenu: Int = R.menu.main_menu_pause
        set(value) {
            field = value
            invalidateOptionsMenu()
        }

    private var task: ScheduleTask? = null

    private val worldStepEventListener = object : StepAdapter() {
        override fun end(step: Step?, world: World?) {
            worldCanvas.postInvalidate()
        }
    }

    private fun Vector2.toJson(): String = json.moshi.adapter(Vector2::class.java).toJson(this)
    private fun String.toVector2(): Vector2 = json.moshi.adapter(Vector2::class.java).fromJson(this)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.tool_bar))

        val floatingButton = findViewById<FloatingActionButton>(R.id.add_floating_button)
        floatingButton.setOnClickListener {
            val intent = Intent(this, AddObjectActivity::class.java)
            intent.putExtra(ARG_WORLD, json.objectAdapter.toJson(map(viewWorld)))
            startActivityForResult(intent, ADD_OBJECT_REQUEST_CODE)
        }
        worldCanvas = findViewById(R.id.world_canvas)
        worldCanvas.viewWorld = viewWorld

        viewWorld.world.addListener(worldStepEventListener)
    }

    override fun onResume() {
        super.onResume()

        if (!noUpdateWorld) {
            val worldFile = File(filesDir, WORLD_FILE_NAME)
            if (worldFile.exists()) {
                val o = json.objectAdapter.fromJson(worldFile.readText())
                viewWorld = unmapViewWorld(o!!)
            }
        }

        if (newGravity != null) {
            viewWorld.world.gravity = newGravity
        }

        noUpdateWorld = false
        newGravity = null
    }

    override fun onPause() {
        super.onPause()

        pauseWorld()

        val worldFile = File(filesDir, WORLD_FILE_NAME)
        worldFile.writeText(json.objectAdapter.toJson(map(viewWorld)))
    }

    override fun onDestroy() {
        super.onDestroy()

        viewWorld.world.removeListener(worldStepEventListener)
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
                viewWorld = ViewWorld()
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
            viewWorld = unmapViewWorld(json.objectAdapter.fromJson(s)!!)
            noUpdateWorld = true
        } else if (requestCode == SETTINGS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val s = data!!.getStringExtra(WorldSettingsActivity.RESULT_GRAVITY)
            newGravity = s.toVector2()
        }
    }

    private fun onWorldChanged(oldWorld: ViewWorld, newWorld: ViewWorld) {
        oldWorld.world.removeListener(worldStepEventListener)
        newWorld.world.addListener(worldStepEventListener)
        worldCanvas.viewWorld = newWorld
        worldCanvas.invalidate()
    }

    private fun onWorldStateChanged(newState: Boolean) {
        optionMenu = if (newState) R.menu.main_menu_play else R.menu.main_menu_pause
    }

    fun runWorld() {
        if (task == null) {
            task = setInterval(1.0 / 60.0) {
                viewWorld.world.update(1.0 / 60.0)
            }
            onWorldStateChanged(true)
        }
    }

    fun pauseWorld() {
        if (task != null) {
            task!!.cancel()
            task = null
            onWorldStateChanged(false)
        }
    }
}
