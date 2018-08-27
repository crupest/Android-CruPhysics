package crupest.cruphysics.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import crupest.cruphysics.R

@SuppressLint("InflateParams", "RtlHardcoded")
class CruPopupMenu(context: Context,
                   menuItemAndHandler: List<Pair<String, () -> Unit>>,
                   width: Int = 500) {

    private val popupWindow: PopupWindow

    init {
        val layoutInflater = LayoutInflater.from(context)
        val rootView = layoutInflater.inflate(R.layout.object_popup_menu, null)
        val list = rootView.findViewById<ListView>(R.id.menu_list)

        val adapter = ArrayAdapter(
                context,
                R.layout.menu_item,
                R.id.content,
                menuItemAndHandler.map { it.first }
        )
        list.adapter = adapter

        popupWindow = PopupWindow(
                rootView,
                width,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        ).apply {
            this.setBackgroundDrawable(ColorDrawable(
                    ContextCompat.getColor(context, R.color.menu_background)
            ))
            this.elevation = 5.0f
        }

        list.setOnItemClickListener { _, _, position, _ ->
            menuItemAndHandler[position].second.invoke()
            popupWindow.dismiss()
        }
    }

    fun show(view: View, x: Int, y: Int) {
        popupWindow.showAsDropDown(view, x, y, Gravity.LEFT or Gravity.TOP)
    }
}
