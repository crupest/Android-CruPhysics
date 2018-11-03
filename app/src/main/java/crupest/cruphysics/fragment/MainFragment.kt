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
import crupest.cruphysics.component.popupMenu
import crupest.cruphysics.data.world.processed.ProcessedWorldRecordForHistory
import crupest.cruphysics.utility.postOnMainThread
import crupest.cruphysics.viewmodel.MainViewModel
import java.text.DateFormat

class MainFragment : BaseFragment() {

    class DrawerFragment : Fragment() {
        var createViewCallback: ((View) -> Unit)? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_main_drawer, container, false)
            createViewCallback?.invoke(rootView)
            return rootView
        }

        companion object {
            fun createInstance(createViewCallback: (View) -> Unit): DrawerFragment = DrawerFragment().apply {
                this.createViewCallback = createViewCallback
            }
        }
    }


    private inner class HistoryAdapter :
            ListLiveDataRecyclerAdapter<ProcessedWorldRecordForHistory, HistoryAdapter.ViewHolder>(
                    this.viewLifecycleOwner, mainViewModel.recordListForHistoryFlow, object : DiffTool<ProcessedWorldRecordForHistory> {
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
            val rootView = LayoutInflater.from(context).inflate(R.layout.item_world_history, parent, false) as CardView
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
                drawer.closeDrawers()
            }
        }
    }

    private lateinit var mainViewModel: MainViewModel

    private lateinit var drawer: DrawerLayout

    private val optionMenuRes = Observable(R.menu.main_menu_pause)

    override fun determineDrawer(activity: IDrawerActivity): Fragment? =
            DrawerFragment.createInstance { rootView ->
                val historyView = rootView.findViewById<RecyclerView>(R.id.history_view)

                historyView.layoutManager = LinearLayoutManager(
                        context, RecyclerView.VERTICAL, false)
                val historyAdapter = HistoryAdapter()
                historyView.adapter = historyAdapter

                mainViewModel.registerWorldHistoryScrollToTopListener(this.viewLifecycleOwner) {
                    postOnMainThread {
                        historyView.scrollToPosition(0)
                    }
                }
            }

    override fun determineOptionMenu(): IOptionMenuActivity.OptionMenuInfo? = dynamicOptionMenu(optionMenuRes) {
        addHandler(R.id.play) {
            mainViewModel.runWorld()
        }
        addHandler(R.id.pause) {
            mainViewModel.pauseWorld()
        }
        addHandler(R.id.create_new) {
            mainViewModel.createNewWorldAndResetCamera()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = context as FragmentActivity
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)

        mainViewModel.worldState.observe(this, Observer {
            optionMenuRes.value = if (it) R.menu.main_menu_play else R.menu.main_menu_pause
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        val worldCanvas = rootView.findViewById<MainWorldCanvas>(R.id.world_canvas)

        worldCanvas.bindViewModel(mainViewModel, this)

        worldCanvas.singleLongTouchListener = {
            val viewModel = mainViewModel

            val body = worldCanvas.viewToWorld(it.x, it.y).run {
                viewModel.bodyHitTest(x, y)
            }

            if (body != null) {
                popupMenu(context!!) {
                    addMenuItem("Delete") {
                        viewModel.removeBody(body)
                    }
                    addMenuItem("Property") {
                        getParentNavigator().navigateTo(EditBodyPropertyFragment.newInstance(body.id))
                    }
                }.show(worldCanvas, it.x.toInt(), it.y.toInt())
            }
        }

        rootView.findViewById<FloatingActionButton>(R.id.add_floating_button).setOnClickListener {
            getParentNavigator().navigateTo(CreateBodyFragment())
        }

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        drawer = (context as IDrawerActivity).getDrawer()
    }

    override fun onPause() {
        super.onPause()

        mainViewModel.pauseWorld()
    }
}
