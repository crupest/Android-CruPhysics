package crupest.cruphysics

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
import crupest.cruphysics.component.drawable.NavigationIconDrawable
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

    private lateinit var navigationButtonDrawable: NavigationIconDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.tool_bar)
        setSupportActionBar(toolbar)

        navigationButtonDrawable = NavigationIconDrawable(this)
        toolbar.navigationIcon = navigationButtonDrawable

        drawer = findViewById(R.id.drawer)
        drawer.isEnabled = false
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        drawer.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            private var init: Boolean = false

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                if (slideOffset.isNaN()) {  // !!! Fuck the damn stupid api!!!!
                    init = true
                    return
                }

                if (init) {
                    init = false
                    return
                }

                navigationButtonDrawable.transformationValue = slideOffset
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
                        navigationButtonDrawable.setState(NavigationIconDrawable.IconState.ARROW, false)
                    } else {
                        navigationButtonDrawable.setState(NavigationIconDrawable.IconState.BURGER, false)
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

    override fun setDrawerFragment(fragment: Fragment?) {
        if (fragment != null) {
            navigationButtonDrawable.isDrawableVisible = true
            navigationButtonDrawable.setState(NavigationIconDrawable.IconState.BURGER)
            toolbar.setNavigationOnClickListener {
                if (isDrawerOpened)
                    drawer.closeDrawer(GravityCompat.START, true)
                else
                    drawer.openDrawer(GravityCompat.START, true)
            }
            drawer.isEnabled = true
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.drawer_content, fragment)
            transaction.commitAllowingStateLoss()
        } else {
            supportFragmentManager.findFragmentById(R.id.drawer_content)?.also {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.remove(it)
                transaction.commitAllowingStateLoss()
                drawer.isEnabled = false
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    override fun setNavigateBackButton(show: Boolean) {
        if (show) {
            navigationButtonDrawable.isDrawableVisible = true
            navigationButtonDrawable.setState(NavigationIconDrawable.IconState.ARROW)
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        } else {
            navigationButtonDrawable.isDrawableVisible = false
            toolbar.setNavigationOnClickListener(null)
        }
    }
}
