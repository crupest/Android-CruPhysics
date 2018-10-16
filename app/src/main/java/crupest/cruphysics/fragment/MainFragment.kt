package crupest.cruphysics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import crupest.cruphysics.NavigationActivity
import crupest.cruphysics.R
import crupest.cruphysics.component.MainWorldCanvas
import crupest.cruphysics.data.world.WorldRecordEntity
import crupest.cruphysics.viewmodel.MainViewModel
import java.text.DateFormat
import java.util.*

class MainFragment : OptionMenuFragment() {

    private inner class HistoryAdapter :
            PagedListAdapter<WorldRecordEntity, HistoryAdapter.ViewHolder>(object :
                    DiffUtil.ItemCallback<WorldRecordEntity>() {
                override fun areItemsTheSame(old: WorldRecordEntity,
                                             new: WorldRecordEntity): Boolean =
                        old.timestamp == new.timestamp

                override fun areContentsTheSame(old: WorldRecordEntity,
                                                new: WorldRecordEntity): Boolean =
                        old.world == new.world && old.camera == new.camera
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
            val record = getItem(position)!!
            holder.timeTextView.text = DateFormat
                    .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(Date(record.timestamp))
            Glide.with(this@MainFragment).load(record.thumbnail).into(holder.worldImageView)
            holder.rootView.setOnClickListener {
                mainViewModel.recoverFromRecord(record)
                mainViewModel.updateTimestamp(record)
                drawer.closeDrawers()
            }
        }
    }

    private lateinit var mainViewModel: MainViewModel

    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = context as FragmentActivity
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)


        mainViewModel.registerWorldStateChangedListener(this) {
            optionMenuRes = if (it) R.menu.main_menu_play else R.menu.main_menu_pause
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        val worldCanvas = rootView.findViewById<MainWorldCanvas>(R.id.world_canvas)

        worldCanvas.bindViewModel(mainViewModel, this)

        mainViewModel.registerThumbnailDelegate(this.viewLifecycleOwner, worldCanvas::generateThumbnail)

        drawer = rootView.findViewById(R.id.drawer)

        val historyView = rootView.findViewById<RecyclerView>(R.id.history_view)
        historyView.layoutManager = LinearLayoutManager(
                context, RecyclerView.VERTICAL, false)
        val historyAdapter = HistoryAdapter()
        mainViewModel.recordList.observe(this, Observer {
            historyAdapter.submitList(it)
        })
        historyView.adapter = historyAdapter

        rootView.findViewById<FloatingActionButton>(R.id.add_floating_button).setOnClickListener {
            val activity = context as NavigationActivity
            activity.navigateToFragment(AddBodyFragment())
        }

        return rootView
    }

    override fun onOptionMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.play -> {
                mainViewModel.runWorld()
                return true
            }
            R.id.pause -> {
                mainViewModel.pauseWorld()
                return true
            }
            R.id.create_new -> {
                mainViewModel.createNewWorldAndResetCamera()
                return true
            }
        }
        return super.onOptionMenuItemSelected(menuItem)
    }
}
