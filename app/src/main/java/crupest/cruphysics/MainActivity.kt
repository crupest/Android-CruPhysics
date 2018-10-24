package crupest.cruphysics

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.balysv.materialmenu.MaterialMenuDrawable
import crupest.cruphysics.fragment.MainFragment
import crupest.cruphysics.fragment.NavigationFragment
import java.lang.IllegalStateException

/**
 * An activity with a single page fragment.
 * Navigate fragment using [navigateToFragment].
 * Please override [onCreate] to navigate to the first fragment using [navigateToFragment].
 */
class MainActivity : AppCompatActivity(), IOptionMenuActivity, IFragmentNavigation, IDrawerActivity, INavigateBackButtonActivity {

    private lateinit var toolbar: Toolbar

    private lateinit var drawer: DrawerLayout
    private var isDrawerOpened: Boolean = false

    private lateinit var navigationButtonDrawable: MaterialMenuDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.tool_bar)
        setSupportActionBar(toolbar)

        navigationButtonDrawable = MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN)
        toolbar.navigationIcon = navigationButtonDrawable

        drawer = findViewById(R.id.drawer)
        drawer.isEnabled = false

        drawer.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                if (drawer.isEnabled) // !!! this function is called oddly so we need to check whether the drawer is enabled!!!
                    navigationButtonDrawable.setTransformationOffset(
                            MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                            if (isDrawerOpened) 2 - slideOffset else slideOffset
                    )
            }

            override fun onDrawerOpened(drawerView: View) {
                isDrawerOpened = true
            }

            override fun onDrawerClosed(drawerView: View) {
                isDrawerOpened = false
            }

            override fun onDrawerStateChanged(newState: Int) {
                if (newState == DrawerLayout.STATE_IDLE) {
                    if (isDrawerOpened) {
                        navigationButtonDrawable.iconState = MaterialMenuDrawable.IconState.ARROW
                    } else {
                        navigationButtonDrawable.iconState = MaterialMenuDrawable.IconState.BURGER
                    }
                }
            }
        })

        navigateToFragment(MainFragment(), false)
    }

    private var optionMenuRes: Int = 0
        set(value) {
            field = value
            invalidateOptionsMenu()
        }

    private var optionMenuItemSelectedListener: ((MenuItem) -> Boolean)? = null
    private var optionMenuLifecycleMap: MutableMap<LifecycleOwner, LifecycleObserver> = mutableMapOf()

    override fun setOptionMenu(lifecycleOwner: LifecycleOwner, menuRes: Observable<Int>, listener: (MenuItem) -> Boolean) {
        if (optionMenuLifecycleMap.containsKey(lifecycleOwner)) {
            lifecycleOwner.lifecycle.removeObserver(optionMenuLifecycleMap[lifecycleOwner]!!)
        }

        val lifecycleObserver = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun setMenu() {
                if (optionMenuItemSelectedListener != null)
                    throw IllegalStateException("Option menu is already set.")

                optionMenuRes = menuRes.value
                menuRes.changeListener = {
                    optionMenuRes = it
                }
                optionMenuItemSelectedListener = listener
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun unsetDrawer() {
                if (optionMenuItemSelectedListener == null)
                    throw IllegalStateException("Option menu is not set yet.")

                optionMenuRes = 0
                menuRes.changeListener = null

                optionMenuItemSelectedListener = null
            }
        }
        optionMenuLifecycleMap[lifecycleOwner] = lifecycleObserver
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (optionMenuRes != 0)
            menuInflater.inflate(optionMenuRes, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            if (optionMenuItemSelectedListener != null)
                optionMenuItemSelectedListener!!.invoke(item!!)
            else
                super.onOptionsItemSelected(item)

    override fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun getFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.fragment_container)
    }

    override fun popBackStack(): Boolean {
        return supportFragmentManager.popBackStackImmediate()
    }

    override fun onBackPressed() {
        // if child fragment is a navigation fragment, first do its navigation.
        val fragment = getFragment()
        if (fragment is NavigationFragment && fragment.popBackStackRecursive())
            return
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun getDrawer(): DrawerLayout {
        return drawer
    }

    override fun setDrawerFragment(lifecycleOwner: LifecycleOwner, drawerFragmentFactory: () -> Fragment) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun setDrawer() {
                if (supportFragmentManager.findFragmentById(R.id.drawer_content) != null)
                    throw IllegalStateException("Drawer fragment is already set.")

                navigationButtonDrawable.isVisible = true
                navigationButtonDrawable.animateIconState(MaterialMenuDrawable.IconState.BURGER)
                toolbar.setNavigationOnClickListener {
                    if (isDrawerOpened)
                        drawer.closeDrawer(GravityCompat.START, true)
                    else
                        drawer.openDrawer(GravityCompat.START, true)
                }

                drawer.isEnabled = true
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.drawer_content, drawerFragmentFactory())
                transaction.commitAllowingStateLoss()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun unsetDrawer() {
                val drawerFragment = supportFragmentManager.findFragmentById(R.id.drawer_content)
                        ?: throw IllegalStateException("No drawer fragment exists.")

                val transaction = supportFragmentManager.beginTransaction()
                transaction.remove(drawerFragment)
                transaction.commitAllowingStateLoss()
                drawer.isEnabled = false
            }
        })
    }

    override fun setNavigateBackButton(show: Boolean) {
        if (show) {
            navigationButtonDrawable.isVisible = true
            navigationButtonDrawable.animateIconState(MaterialMenuDrawable.IconState.ARROW)
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        } else {
            navigationButtonDrawable.isVisible = false
            toolbar.setNavigationOnClickListener(null)
        }
    }
}
