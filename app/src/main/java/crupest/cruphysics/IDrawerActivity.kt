package crupest.cruphysics

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

interface IDrawerActivity {
    fun getDrawer(): DrawerLayout
    fun setDrawerFragment(lifecycleOwner: LifecycleOwner, drawerFragmentFactory: () -> Fragment)
}
