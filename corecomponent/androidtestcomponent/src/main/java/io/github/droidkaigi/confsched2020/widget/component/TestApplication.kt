package io.github.droidkaigi.confsched2020.widget.component

import android.app.Application
import com.wada811.dependencyproperty.DependencyModule
import com.wada811.dependencyproperty.DependencyModules
import com.wada811.dependencyproperty.DependencyModulesHolder

class TestApplication(vararg modules: DependencyModule) : Application(), DependencyModulesHolder {
    override val dependencyModules: DependencyModules by dependencyModules(*modules)
}
