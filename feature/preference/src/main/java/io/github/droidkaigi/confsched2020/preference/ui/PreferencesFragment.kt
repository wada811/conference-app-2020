package io.github.droidkaigi.confsched2020.preference.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.github.droidkaigi.confsched2020.model.NightMode
import io.github.droidkaigi.confsched2020.preference.R
import io.github.droidkaigi.confsched2020.preference.ui.viewmodel.PreferenceViewModel
import io.github.droidkaigi.confsched2020.R as MainR

class PreferencesFragment : PreferenceFragmentCompat() {

    private val preferenceViewModel: PreferenceViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager?.findPreference<ListPreference>(DARK_THEME_KEY)?.also {
            preferenceViewModel.setNightMode(it.value.toNightMode())
            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                preferenceViewModel.setNightMode((newValue as String).toNightMode())
                return@OnPreferenceChangeListener true
            }
        }

        preferenceViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            AppCompatDelegate.setDefaultNightMode(uiModel.nightMode.platformValue)
        }
    }

    // region temporary functions until appropriate structure have built
    private fun String.toNightMode() = when (this) {
        getString(MainR.string.pref_theme_value_default) -> NightMode.SYSTEM
        getString(MainR.string.pref_theme_value_battery) -> NightMode.BATTERY
        getString(MainR.string.pref_theme_value_dark) -> NightMode.YES
        getString(MainR.string.pref_theme_value_light) -> NightMode.NO
        else -> throw IllegalArgumentException("should not happen")
    }

    private val NightMode.platformValue: Int
        get() = when (this) {
            NightMode.SYSTEM -> {
                if (Build.VERSION.SDK_INT < 29) AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            NightMode.BATTERY -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            NightMode.YES -> AppCompatDelegate.MODE_NIGHT_YES
            NightMode.NO -> AppCompatDelegate.MODE_NIGHT_NO
        }
    // endregion

    companion object {
        private const val DARK_THEME_KEY = "darkTheme"
    }
}
