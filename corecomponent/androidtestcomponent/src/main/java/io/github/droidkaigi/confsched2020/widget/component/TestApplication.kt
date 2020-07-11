package io.github.droidkaigi.confsched2020.widget.component

import android.app.Application
import com.wada811.dependencyproperty.DependencyContext
import com.wada811.dependencyproperty.DependencyModule
import com.wada811.dependencyproperty.DependencyModules

class TestApplication(vararg modules: DependencyModule) : Application(), DependencyContext {
    override val dependencyModules: DependencyModules by dependencyModules(*modules)
}
