package io.github.droidkaigi.confsched2020.initializer

import android.app.Application

class AppInitializers(
    private vararg val initializers: AppInitializer
) {
    fun initialize(application: Application) {
        initializers.forEach {
            it.initialize(application)
        }
    }
}
