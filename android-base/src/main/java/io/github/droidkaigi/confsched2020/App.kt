package io.github.droidkaigi.confsched2020

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import com.wada811.dependencyproperty.DependencyContext
import com.wada811.dependencyproperty.DependencyModules
import io.github.droidkaigi.confsched2020.data.repository.di.RepositoryModule
import io.github.droidkaigi.confsched2020.initializer.AppInitializers
import io.github.droidkaigi.confsched2020.initializer.CoilInitializer
import io.github.droidkaigi.confsched2020.initializer.EmojiInitializer
import io.github.droidkaigi.confsched2020.initializer.FirebaseMessagingInitializer
import io.github.droidkaigi.confsched2020.initializer.FirestoreInitializer
import io.github.droidkaigi.confsched2020.initializer.ThemeInitializer
import io.github.droidkaigi.confsched2020.initializer.TimberInitializer
import io.github.droidkaigi.confsched2020.session.di.SessionModule

open class App : Application(), DependencyContext {
    @Suppress("LeakingThis")
    override val dependencyModules: DependencyModules by dependencyModules(
        RepositoryModule(this),
        SessionModule(this)
    )

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }

    private val initializers: AppInitializers by lazy {
        AppInitializers(
            CoilInitializer(),
            EmojiInitializer(),
            FirebaseMessagingInitializer(),
            FirestoreInitializer(),
            ThemeInitializer(),
            TimberInitializer()
        )
    }

    override fun onCreate() {
        super.onCreate()
        initializers.initialize(this)
    }
}
