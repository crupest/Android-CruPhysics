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
import androidx.fragment.app.FragmentTransaction
import crupest.cruphysics.component.drawable.NavigationIconDrawable
import crupest.cruphysics.fragment.BaseFragment
import crupest.cruphysics.fragment.MainFragment
import crupest.cruphysics.utility.postDelayOnMainThread
import crupest.cruphysics.utility.weakReference
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), IFragmentNavigator, IOptionMenuActivity, IDrawerActivity, INavigationButtonActivity {

    private enum class FragmentInfoKey {
        OPTION_MENU,
        DRAWER,
        NAVIGATION_BUTTON
    }

    private lateinit var toolbar: Toolbar

    private lateinit var drawer: DrawerLayout
    private var isDrawerOpened: Boolean = false

    private lateinit var navigationButtonDrawable: NavigationIconDrawable

    private val fragmentInfoCache = mutableMapOf<FragmentInfoKey, WeakReference<BaseFragment>>()


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
        val old = this.optionMenuInfo
        old?.apply { menuRes.changeListener = null }

        this.optionMenuInfo = optionMenuInfo
        if (optionMenuInfo != null) {
            optionMenuRes = optionMenuInfo.menuRes.value
            optionMenuInfo.menuRes.changeListener = {
                optionMenuRes = it
            }
        } else {
            optionMenuRes = 0
        }
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
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        if (addToBackStack) {
            transaction.addToBackStack(null)
            transaction.commit()
            supportFragmentManager.executePendingTransactions()
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
        postDelayOnMainThread(1) {
            onNavigated()
        }
    }

    private fun onNavigated() {

        var fragment: BaseFragment = getCurrentFragment()

        val determinedSet = mutableSetOf<FragmentInfoKey>()

        fun determine(name: FragmentInfoKey, fragment: BaseFragment, action: (BaseFragment) -> Boolean) {
            if (name in determinedSet)
                return

            if (fragmentInfoCache[name] == fragment) {
                determinedSet.add(name)
                return
            }

            val result = action(fragment)
            if (result) {
                determinedSet.add(name)
                fragmentInfoCache[name] = fragment.weakReference()
            }
        }

        fun <T : Any> T?.ifNotNull(block: (T) -> Unit): Boolean {
            return if (this == null) false else {
                block(this)
                true
            }
        }

        while (true) {
            determine(FragmentInfoKey.OPTION_MENU, fragment) { f ->
                f.determineOptionMenu().ifNotNull { setOptionMenu(it) }
            }

            determine(FragmentInfoKey.DRAWER, fragment) { f->
                f.determineDrawer(this).ifNotNull { setDrawerFragment(it) }
            }

            determine(FragmentInfoKey.NAVIGATION_BUTTON, fragment) { f ->
                f.determineNavigationButton().ifNotNull { setNavigationButton(it) }
            }

            if (FragmentInfoKey.values().all { it in determinedSet })
                break

            if (fragment is IFragmentNavigator)
                fragment = fragment.getCurrentFragment()
            else
                break
        }

        fun checkDetermine(key: FragmentInfoKey, onUndetermined: () -> Unit) {
            if (key !in determinedSet) {
                fragmentInfoCache.remove(key)
                onUndetermined()
            }
        }

        checkDetermine(FragmentInfoKey.OPTION_MENU) { setOptionMenu(null) }
        checkDetermine(FragmentInfoKey.DRAWER) { setDrawerFragment(null) }
        checkDetermine(FragmentInfoKey.NAVIGATION_BUTTON) {
            setNavigationButton(if (FragmentInfoKey.DRAWER in determinedSet) INavigationButtonActivity.Button.MENU else INavigationButtonActivity.Button.BACK)
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
