package crupest.cruphysics

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

interface IDrawerActivity {
    fun getDrawer(): DrawerLayout
    fun setDrawerFragment(fragment: Fragment?)
}
