package io.github.droidkaigi.confsched2020

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.graphics.drawable.RippleDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.splitcompat.SplitCompat
import com.wada811.dependencyproperty.dependency
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.data.repository.di.RepositoryModule
import io.github.droidkaigi.confsched2020.databinding.ActivityMainBinding
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.ext.stringRes
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.ui.PageConfiguration
import io.github.droidkaigi.confsched2020.ui.widget.SystemUiManager
import io.github.droidkaigi.confsched2020.widget.component.NavigationDirections.Companion.actionGlobalToChrome
import timber.log.Timber
import timber.log.warn

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
    }
    private val systemViewModel: SystemViewModel by viewModels()
    private val sessionRepository by dependency<RepositoryModule, SessionRepository> { it.sessionRepository }
    private val navController: NavController by lazy {
        Navigation.findNavController(this, R.id.root_nav_host_fragment)
    }

    private val statusBarColors: SystemUiManager by lazy {
        SystemUiManager(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.installActivity(this)
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        // Workaround for crash on 5.x and 6.x after install dfm
        // https://issuetracker.google.com/issues/147937971
        if (Build.VERSION.SDK_INT in 21..23) {
            return
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setupNavigation()
        setupStatusBarColors()
        setupShortcuts()

        binding.drawerLayout.doOnApplyWindowInsets { _, insets, _ ->
            binding.drawerLayout.setChildInsetsWorkAround(insets)
        }
        binding.contentContainer.doOnApplyWindowInsets { view, insets, initialState ->
            view.updatePadding(
                left = insets.systemWindowInsetLeft + initialState.paddings.left,
                right = insets.systemWindowInsetRight + initialState.paddings.right
            )
        }
        binding.toolbar.doOnApplyWindowInsets { view, insets, initialState ->
            view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = insets.systemWindowInsetTop + initialState.margins.top
            }
        }
        binding.toolbar.doOnLayout {
            // Invalidate because option menu cannot be displayed after screen rotation
            invalidateOptionsMenu()
        }
        binding.navView.doOnApplyWindowInsets { view, insets, initialState ->
            view.apply {
                // On seascape mode only, nav bar is overlapped with DrawerLayout.
                // So set left padding and reset width.
                val leftSpace = insets.systemWindowInsetLeft + initialState.paddings.left
                updatePadding(left = leftSpace)
                updateLayoutParams {
                    if (getWidth() > 0) {
                        width = measuredWidth + leftSpace
                    }
                }
            }
        }

        systemViewModel.errorLiveData.observe(this) { appError ->
            Timber.warn(appError) { "AppError occurred" }
            Snackbar
                .make(
                    findViewById(R.id.root_nav_host_fragment),
                    appError.stringRes(),
                    Snackbar.LENGTH_LONG
                )
                .show()
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun DrawerLayout.setChildInsetsWorkAround(insets: WindowInsetsCompat) {
        // If we use fitSystemWindows, DrawerLayout will consume windowInsets.
        // But we want to apply window insets in other places. So we use the inner method for padding
        setChildInsets(
            insets.toWindowInsets(), insets.systemWindowInsetTop > 0
        )
    }

    private fun setupNavigation() {
        val appBarConfiguration = AppBarConfiguration(
            PageConfiguration.values().filter { it.isTopLevel }.map { it.id }.toSet(),
            binding.drawerLayout
        ) {
            onBackPressed()
            true
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.setNavigationItemSelectedListener { item ->
            handleNavigation(item.itemId)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            onDestinationChange(destination)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        binding.toolbar.children.forEach {
            when (it) {
                is ActionMenuView -> {
                    it.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                        it.children.filterIsInstance<ActionMenuItemView>().forEach { menuItemView ->
                            setRippleColor(menuItemView, binding.isIndigoBackground)
                        }
                    }
                }
                is AppCompatImageButton -> setRippleColor(it, binding.isIndigoBackground)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun setRippleColor(view: View, isIndigoBackground: Boolean) {
        (view.background as? RippleDrawable)?.setColor(
            ColorStateList.valueOf(
                this.getThemeColor(
                    if (isIndigoBackground) {
                        R.attr.colorOnPrimary
                    } else {
                        R.attr.colorControlHighlight
                    }
                )
            )
        )
    }

    private fun onDestinationChange(destination: NavDestination) {
        binding.navView.menu.findItem(destination.id)?.isChecked = true

        val config = PageConfiguration.getConfiguration(destination.id)
        if (!config.hasTitle) {
            supportActionBar?.title = ""
        }
        if (config.isShowLogoImage) {
            supportActionBar?.setLogo(R.drawable.ic_logo)
        } else {
            supportActionBar?.setLogo(null)
        }
        statusBarColors.isIndigoBackground = config.isIndigoBackground
        binding.isIndigoBackground = config.isIndigoBackground
        val iconTint = getThemeColor(
            if (config.isIndigoBackground) {
                R.attr.colorOnPrimary
            } else {
                R.attr.colorOnSurface
            }
        )
        binding.toolbar.navigationIcon = if (config.isTopLevel) {
            AppCompatResources.getDrawable(this, R.drawable.ic_menu_black_24dp)
        } else {
            AppCompatResources.getDrawable(this, R.drawable.ic_arrow_back_black_24dp)
        }.apply {
            this?.setTint(iconTint)
        }
    }

    private fun setupStatusBarColors() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                statusBarColors.drawerSlideOffset = slideOffset
            }
        })

        statusBarColors.systemUiVisibility.distinctUntilChanged().observe(this) { visibility ->
            window.decorView.systemUiVisibility = visibility
        }
        statusBarColors.statusBarColor.distinctUntilChanged().observe(this) { color ->
            window.statusBarColor = color
        }
        statusBarColors.navigationBarColor.distinctUntilChanged().observe(this) { color ->
            window.navigationBarColor = color
        }
    }

    private fun setupShortcuts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return
        val shortcutManager = getSystemService<ShortcutManager>(ShortcutManager::class.java)

        val map = ShortcutInfo.Builder(this, "map")
            .setShortLabel(getString(R.string.floor_map_shortcut_short_label1))
            .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
            .setIntent(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://droidkaigi.jp/2020/floormap")
                ).setComponent(ComponentName(this, MainActivity::class.java))
            )
            .build()
        val myPlan = ShortcutInfo.Builder(this, "my_plan")
            .setShortLabel(getString(R.string.my_plan_shortcut_short_label1))
            .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
            .setIntent(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://droidkaigi.jp/2020/main/3")
                ).setComponent(ComponentName(this, MainActivity::class.java))
            )
            .build()
        shortcutManager?.addDynamicShortcuts(listOf(map, myPlan))
    }

    private fun handleNavigation(@IdRes itemId: Int): Boolean {
        binding.drawerLayout.closeDrawers()

        when (itemId) {
            R.id.entire_survey -> {
                navController.navigate(actionGlobalToChrome(ENTIRE_SURVEY))
                return true
            }
        }

        return try {
            // ignore if current destination is selected
            if (navController.currentDestination?.id == itemId) return false
            val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.main, false)
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
            val options = builder.build()
            navController.navigate(itemId, null, options)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    companion object {
        const val ENTIRE_SURVEY =
            "https://docs.google.com/forms/d/e/1FAIpQLSfQHIwT0lf-20tx5xgUFSm7PPy_EjD5lI8SHuxV3DHN4D9pkA/viewform" // ktlint-disable max-line-length
    }
}
