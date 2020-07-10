package io.github.droidkaigi.confsched2020.data.device.di

import android.content.Context
import com.wada811.dependencyproperty.DependencyModule
import io.github.droidkaigi.confsched2020.data.device.WifiManager
import io.github.droidkaigi.confsched2020.data.device.internal.AndroidWifiManager

class DeviceModule(private val context: Context) : DependencyModule {
    val wifiManager: WifiManager by lazy {
        AndroidWifiManager(
            context
        )
    }
}
