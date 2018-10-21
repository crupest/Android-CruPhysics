package crupest.cruphysics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import crupest.cruphysics.*
import crupest.cruphysics.component.MainWorldCanvas
import crupest.cruphysics.component.adapter.ListLiveDataRecyclerAdapter
import crupest.cruphysics.data.world.processed.ProcessedWorldRecordForHistory
import crupest.cruphysics.utility.postOnMainThread
import crupest.cruphysics.viewmodel.MainViewModel
import java.text.DateFormat

class MainFragment : Fragment() {

    class DrawerFragment : Fragment() {
        var createViewCallback: ((View) -> Unit)? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_main_drawer, container, false)
            createViewCallback?.invoke(rootView)
            return rootView
        }
    }


    private inner class HistoryAdapter :
            ListLiveDataRecyclerAdapter<ProcessedWorldRecordForHistory, HistoryAdapter.ViewHolder>(
                    this, mainViewModel.recordListForHistoryFlow, object : DiffTool<ProcessedWorldRecordForHistory> {
                override fun areItemSame(oldOne: ProcessedWorldRecordForHistory,
                                         newOne: ProcessedWorldRecordForHistory): Boolean =
                        oldOne.id == newOne.id

                override fun areContentSame(oldOne: ProcessedWorldRecordForHistory, newOne: ProcessedWorldRecordForHistory): Boolean =
                        oldOne.timestamp == newOne.timestamp
            }) {

        inner class ViewHolder(val rootView: CardView) : RecyclerView.ViewHolder(rootView) {
            val timeTextView: TextView = itemView.findViewById(R.id.time)
            val worldImageView: ImageView = itemView.findViewById(R.id.world)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val rootView = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false) as CardView
            return ViewHolder(rootView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val record = getItem(position)
            holder.timeTextView.text = DateFormat
                    .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(record.timestamp)
            holder.worldImageView.setImageBitmap(record.thumbnail)
            holder.rootView.setOnClickListener {
                mainViewModel.recoverFromRecordAndUpdateTimestamp(record)
                postOnMainThread {
                    historyView.scrollToPosition(0)
                }
                drawer.closeDrawers()
            }
        }
    }

    private lateinit var mainViewModel: MainViewModel

    private lateinit var drawer: DrawerLayout
    private lateinit var historyView: RecyclerView

    private val optionMenuRes = Observable(R.menu.main_menu_pause)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (context as IOptionMenuActivity).setOptionMenu(this, optionMenuRes) {
            when (it.itemId) {
                R.id.play -> {
                    mainViewModel.runWorld()
                    true
                }
                R.id.pause -> {
                    mainViewModel.pauseWorld()
                    true
                }
                R.id.create_new -> {
                    mainViewModel.createNewWorldAndResetCamera()
                    true
                }
                else -> false
            }
        }

        val activity = context as FragmentActivity
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)

        mainViewModel.worldState.observe(this, Observer {
            optionMenuRes.value = if (it) R.menu.main_menu_play else R.menu.main_menu_pause
        })

        (context as IDrawerActivity).setDrawerFragment(this) {
            DrawerFragment().apply {
                createViewCallback = { rootView ->
                    val historyView = rootView.findViewById<RecyclerView>(R.id.history_view)
                    this@MainFragment.historyView = historyView
                    historyView.layoutManager = LinearLayoutManager(
                            context, RecyclerView.VERTICAL, false)
                    val historyAdapter = HistoryAdapter()
                    historyView.adapter = historyAdapter
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        val worldCanvas = rootView.findViewById<MainWorldCanvas>(R.id.world_canvas)

        worldCanvas.bindViewModel(mainViewModel, this)

        rootView.findViewById<FloatingActionButton>(R.id.add_floating_button).setOnClickListener {
            val activity = context as MainActivity
            activity.navigateToFragment(AddBodyFragment())
        }

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        drawer = (context as IDrawerActivity).getDrawer()
    }
}
