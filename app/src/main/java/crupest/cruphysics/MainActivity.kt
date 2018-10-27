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
import crupest.cruphysics.component.drawable.NavigationIconDrawable
import crupest.cruphysics.fragment.BaseFragment
import crupest.cruphysics.fragment.MainFragment
import crupest.cruphysics.utility.postOnMainThread

class MainActivity : AppCompatActivity(), IFragmentNavigator, IOptionMenuActivity, IDrawerActivity, INavigationButtonActivity {

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

        if (savedInstanceState == null)
            navigateToFragment(MainFragment(), false)
        notifyNavigated() // also notify when recreate.
    }

    private var optionMenuRes: Int = 0
        set(value) {
            field = value
            invalidateOptionsMenu()
        }

    private var optionMenuInfo: IOptionMenuActivity.OptionMenuInfo? = null

    override fun setOptionMenu(optionMenuInfo: IOptionMenuActivity.OptionMenuInfo?) {
        this.optionMenuInfo = optionMenuInfo
        optionMenuRes = optionMenuInfo?.menuRes?.value ?: 0
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (optionMenuRes != 0)
            menuInflater.inflate(optionMenuRes, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            if (optionMenuInfo != null && optionMenuInfo!!.handlers.containsKey(item!!.itemId)) {
                optionMenuInfo!!.handlers[item.itemId]!!.invoke()
                true
            } else
                super.onOptionsItemSelected(item)

    private fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
            transaction.commit()
        } else
            transaction.commitNow()
    }

    override fun navigateTo(fragment: BaseFragment) {
        navigateToFragment(fragment, true)
    }

    override fun getCurrentFragment(): BaseFragment =
            (supportFragmentManager.findFragmentById(R.id.fragment_container)
                    ?: throw IllegalStateException("Fragment is not loaded.")) as BaseFragment

    override fun popBackStack(): Boolean {
        val result = supportFragmentManager.popBackStackImmediate()
        notifyNavigated()
        return result
    }

    internal fun notifyNavigated() {
        postOnMainThread {
            onNavigated()
        }
    }

    private fun onNavigated() {
        var fragment: BaseFragment = getCurrentFragment()

        var optionMenuDetermined = false
        var drawerDetermined = false
        var navigationButtonDetermined = false


        while (true) {

            if (!optionMenuDetermined) {
                val optionMenu = fragment.determineOptionMenu()
                if (optionMenu != null) {
                    setOptionMenu(optionMenu)
                    optionMenuDetermined = true
                }
            }

            if (!drawerDetermined) {
                val drawerFragment = fragment.determineDrawer(this)
                if (drawerFragment != null) {
                    setDrawerFragment(drawerFragment)
                    drawerDetermined = true
                }
            }

            if (!navigationButtonDetermined) {
                val button = fragment.determineNavigationButton()
                if (button != null) {
                    setNavigationButton(button)
                    navigationButtonDetermined = true
                }
            }

            if (optionMenuDetermined && drawerDetermined && navigationButtonDetermined)
                break

            if (fragment is IFragmentNavigator)
                fragment = fragment.getCurrentFragment()
            else
                break
        }

        if (!optionMenuDetermined) {
            setOptionMenu(null)
        }

        if (!drawerDetermined) {
            setDrawerFragment(null)
        }

        if (!navigationButtonDetermined) {
            setNavigationButton(if (drawerDetermined) INavigationButtonActivity.Button.MENU else INavigationButtonActivity.Button.BACK)
        }
    }

    override fun onBackPressed() {
        val fragment = getCurrentFragment()
        if (fragment is IFragmentNavigator && fragment.popBackStackRecursive())
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
            drawer.isEnabled = true
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.drawer_content, fragment)
            transaction.commitNowAllowingStateLoss()
        } else {
            supportFragmentManager.findFragmentById(R.id.drawer_content)?.also {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.remove(it)
                transaction.commitNowAllowingStateLoss()
                drawer.isEnabled = false
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    override fun setNavigationButton(button: INavigationButtonActivity.Button) {
        when (button) {
            INavigationButtonActivity.Button.NONE -> {
                navigationButtonDrawable.isDrawableVisible = false
                toolbar.setNavigationOnClickListener(null)

            }
            INavigationButtonActivity.Button.BACK -> {
                navigationButtonDrawable.isDrawableVisible = true
                navigationButtonDrawable.setState(NavigationIconDrawable.IconState.ARROW)
                toolbar.setNavigationOnClickListener {
                    onBackPressed()
                }
            }
            INavigationButtonActivity.Button.MENU -> {
                navigationButtonDrawable.isDrawableVisible = true
                navigationButtonDrawable.setState(NavigationIconDrawable.IconState.BURGER)
                toolbar.setNavigationOnClickListener {
                    if (isDrawerOpened)
                        drawer.closeDrawer(GravityCompat.START, true)
                    else
                        drawer.openDrawer(GravityCompat.START, true)
                }
            }
        }
    }
}
