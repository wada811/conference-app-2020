package io.github.droidkaigi.confsched2020.initializer

import android.app.Application
import io.github.droidkaigi.confsched2020.image.CoilInitializer

class CoilInitializer : AppInitializer {
    override fun initialize(application: Application) {
        CoilInitializer.init(application)
    }
}
