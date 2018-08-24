package crupest.cruphysics

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.MenuRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import crupest.cruphysics.component.IMainWorldDelegate
import crupest.cruphysics.component.MainWorldCanvas
import crupest.cruphysics.serialization.data.BodyData
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.WorldData
import crupest.cruphysics.data.world.WorldRecord
import crupest.cruphysics.data.world.WorldRepository
import crupest.cruphysics.physics.view.WorldViewData
import crupest.cruphysics.serialization.fromData
import crupest.cruphysics.serialization.fromJson
import crupest.cruphysics.serialization.toData
import crupest.cruphysics.serialization.toJson
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.setInterval
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.World
import org.dyn4j.geometry.Vector2
import java.io.File
import java.text.DateFormat
import java.util.*


/**
 * Activity class [MainActivity].
 * Represents the main activity.
 */
class MainActivity : AppCompatActivity(), IMainWorldDelegate, IWorldRecordFileResolver {

    companion object {
        const val ARG_WORLD = "WORLD"
        const val ARG_CAMERA = "CAMERA"
        const val ADD_OBJECT_REQUEST_CODE = 2000


        const val THUMBNAIL_FILE_DIR_NAME = "thumbnails"

        const val THUMBNAIL_WIDTH = 1000
        const val THUMBNAIL_HEIGHT = 500
    }

    private inner class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

        override fun getItemCount(): Int = worldRepository.recordCount

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val rootView = LayoutInflater.from(this@MainActivity).inflate(
                    R.layout.history_item,
                    parent,
                    false
            ) as CardView
            return ViewHolder(rootView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val record = worldRepository.getRecord(position)
            holder.timeTextView.text = DateFormat
                    .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(Date(record.timestamp))
            Picasso.with(this@MainActivity)
                    .load(getThumbnailFile(record.thumbnailFile))
                    .fit()
                    .into(holder.worldImageView)
            holder.rootView.setOnClickListener {
                recoverFromRecord(record)
                // You can't just use position here, because it may remain unchanged when the item
                // is moved (because it won't rebind). So you always have to get the latest position.
                // This has cost my 30 minutes.
                worldRepository.updateTimestamp(holder.adapterPosition)
                drawer.closeDrawers()
            }
        }

        inner class ViewHolder(val rootView: CardView) : RecyclerView.ViewHolder(rootView) {
            val timeTextView: TextView = itemView.findViewById(R.id.time)
            val worldImageView: ImageView = itemView.findViewById(R.id.world)
        }
    }

    //Region: properties ui

    private lateinit var worldCanvas: MainWorldCanvas
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var drawer: DrawerLayout

    @get:MenuRes
    @setparam:MenuRes
    @field:MenuRes
    private var optionMenu: Int = R.menu.main_menu_pause
        set(value) {
            field = value
            invalidateOptionsMenu()
        }


    //Region: properties world

    private lateinit var world: World
    private var task: ScheduleTask? = null

    private lateinit var worldViewData: WorldViewData

    //Region: properties data
    private lateinit var worldRepository: WorldRepository

    private val thumbnailDir: File by lazy {
        getDir(THUMBNAIL_FILE_DIR_NAME, MODE_PRIVATE)
    }

    //Region: methods ui

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.tool_bar))

        worldRepository = WorldRepository(applicationContext, this)

        drawer = findViewById(R.id.drawer)

        val historyView: RecyclerView = findViewById(R.id.history)
        historyView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyAdapter = HistoryAdapter()
        historyView.adapter = historyAdapter

        val floatingButton = findViewById<FloatingActionButton>(R.id.add_floating_button)
        floatingButton.setOnClickListener {
            val intent = Intent(this, AddBodyActivity::class.java)
            intent.putExtra(AddBodyActivity.ARG_WORLD, world.toData().toJson())
            intent.putExtra(AddBodyActivity.ARG_CAMERA, worldCanvas.generateCameraData().toJson())
            startActivityForResult(intent, ADD_OBJECT_REQUEST_CODE)
        }

        worldCanvas = findViewById(R.id.world_canvas)
        worldCanvas.mainWorldDelegate = this

        if (savedInstanceState == null) {
            if (worldRepository.recordCount == 0)
                createNewWorld()
            else
                recoverFromRecord(worldRepository.getRecord(0))
        } else {
            val worldData: WorldData = savedInstanceState.getString(ARG_WORLD).fromJson()
            val cameraData: CameraData = savedInstanceState.getString(ARG_CAMERA).fromJson()

            recoverFromData(worldData, cameraData)
        }

        worldRepository.addCompleteListener = {
            runOnUiThread {
                historyAdapter.notifyItemInserted(0)
                historyView.scrollToPosition(0)
                it.notifyDone()
            }
        }
        worldRepository.latestCameraUpdateCompleteListener = {
            runOnUiThread {
                historyAdapter.notifyItemChanged(0)
                it.notifyDone()
            }
        }
        worldRepository.timestampUpdateCompleteListener =  {
            runOnUiThread {
                historyAdapter.notifyItemMoved(it.oldPosition, 0)
                historyView.scrollToPosition(0)
                it.notifyDone()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (!pauseWorld() && !world.isEmpty)
            worldRepository.updateLatestRecordCamera(worldCanvas.generateCameraData(), generateThumbnail())
    }

    override fun onDestroy() {
        super.onDestroy()

        worldRepository.closeAndWait()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState!!.putString(ARG_WORLD, world.toData().toJson())
        outState.putString(ARG_CAMERA, worldCanvas.generateCameraData().toJson())
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
            val bodyData: BodyData = data!!.getStringExtra(AddBodyActivity.RESULT_BODY).fromJson()
            val cameraData: CameraData = data.getStringExtra(AddBodyActivity.RESULT_CAMERA).fromJson()

            addBody(bodyData.fromData())
            worldCanvas.setCamera(cameraData)
            //Directly save without checking dirty.
            saveCurrentWorldToDatabase()
            worldCanvas.repaint()
        }
    }


    //Region: world

    private fun createNewWorld() {
        world = World()
        worldViewData = WorldViewData()
        worldCanvas.drawWorldDelegate = worldViewData
        worldCanvas.resetCamera()

        worldCanvas.repaint()
    }

    private fun onWorldStateChanged(newState: Boolean) {
        optionMenu = if (newState) R.menu.main_menu_play else R.menu.main_menu_pause
    }

    private fun runWorld(): Boolean =
        if (task == null) {
            task = setInterval(1.0 / 60.0) {
                world.update(1.0 / 60.0)
                worldCanvas.repaint()
            }
            onWorldStateChanged(true)
            true
        } else false

    private fun pauseWorld() =
        if (task != null) {
            task!!.cancel()
            task = null
            onWorldStateChanged(false)
            saveCurrentWorldToDatabase()
            true
        } else false

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

    override fun getThumbnailFile(fileName: String): File = thumbnailDir.resolve(fileName)

    private fun generateThumbnail(): Bitmap =
            worldViewData.generateThumbnail(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT,
                    worldCanvas.getThumbnailViewMatrix(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, 0.5f))

    private fun recoverFromData(worldData: WorldData, cameraData: CameraData) {
        world = worldData.fromData()
        worldCanvas.setCamera(cameraData)
        worldViewData = WorldViewData(world.bodies)
        worldCanvas.drawWorldDelegate = worldViewData
        worldCanvas.repaint()
    }

    private fun recoverFromRecord(worldRecord: WorldRecord) {
        recoverFromData(worldRecord.world.fromJson(), worldRecord.camera.fromJson())
    }

    private fun saveCurrentWorldToDatabase() {
        worldRepository.addRecord(
                world.toData(),
                worldCanvas.generateCameraData(),
                generateThumbnail()
        )
    }
}
