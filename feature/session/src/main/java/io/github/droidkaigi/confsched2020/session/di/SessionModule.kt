package io.github.droidkaigi.confsched2020.session.di

import android.app.Application
import com.wada811.dependencyproperty.DependencyModule
import io.github.droidkaigi.confsched2020.session.util.SessionAlarm

class SessionModule(application: Application) : DependencyModule {
    val sessionAlarm by lazy {
        SessionAlarm(application)
    }
}
