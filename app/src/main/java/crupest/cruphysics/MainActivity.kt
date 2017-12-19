package crupest.cruphysics

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import crupest.cruphysics.component.WorldCanvas
import crupest.cruphysics.physics.WorldChangeEventArgs
import crupest.cruphysics.physics.WorldManager
import crupest.cruphysics.physics.WorldStateChangeEventArgs
import org.dyn4j.dynamics.Step
import org.dyn4j.dynamics.StepAdapter
import org.dyn4j.dynamics.World
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        val worldFileName = "world.wld"
    }

    private var worldCanvas: WorldCanvas? = null

    private var optionMenu: Int = R.menu.main_menu_pause
        set(value) {
            field = value
            invalidateOptionsMenu()
        }

    private val worldStateChangeEventListener: (WorldStateChangeEventArgs) -> Unit = {
        optionMenu = if (it.newState) R.menu.main_menu_play else R.menu.main_menu_pause
    }

    private val worldStepEventListener = object : StepAdapter() {
        override fun end(step: Step?, world: World?) {
            worldCanvas?.postInvalidate()
        }
    }

    private val worldChangeEventListener: (WorldChangeEventArgs) -> Unit = {
        it.oldWorld.removeListener(worldStepEventListener)
        it.newWorld.addListener(worldStepEventListener)
        worldCanvas?.invalidate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.tool_bar))

        val floatingButton = findViewById<FloatingActionButton>(R.id.add_floating_button)
        floatingButton.setOnClickListener {
            val intent = Intent(this, AddObjectActivity::class.java)
            startActivity(intent)
        }
        worldCanvas = findViewById(R.id.world_canvas)

        WorldManager.worldChangeEvent.addListener(worldChangeEventListener)
        WorldManager.worldStateChangeEvent.addListener(worldStateChangeEventListener)
        WorldManager.world.addListener(worldStepEventListener)

        val worldFile = File(filesDir, worldFileName)
        if (worldFile.exists())
            WorldManager.readFromFile(worldFile)
    }

    override fun onResume() {
        super.onResume()
        worldCanvas!!.myMatrix.set(WorldManager.viewMatrix)
        worldCanvas!!.invalidate()
    }

    override fun onPause() {
        super.onPause()

        WorldManager.pauseWorld()
        WorldManager.viewMatrix.set(worldCanvas!!.myMatrix)
    }

    override fun onDestroy() {
        super.onDestroy()

        WorldManager.worldChangeEvent.removeListener(worldChangeEventListener)
        WorldManager.worldStateChangeEvent.removeListener(worldStateChangeEventListener)
        WorldManager.world.removeListener(worldStepEventListener)

        val worldFile = File(filesDir, worldFileName)
        WorldManager.saveToFile(worldFile)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(optionMenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.play -> {
                WorldManager.runWorld()
                return true
            }
            R.id.pause -> {
                WorldManager.pauseWorld()
                return true
            }
            R.id.create_new -> {
                WorldManager.createNewWorld()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
