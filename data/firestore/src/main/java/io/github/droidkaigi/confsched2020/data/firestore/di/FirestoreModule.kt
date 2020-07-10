package io.github.droidkaigi.confsched2020.data.firestore.di

import com.wada811.dependencyproperty.DependencyModule
import io.github.droidkaigi.confsched2020.data.firestore.Firestore
import io.github.droidkaigi.confsched2020.data.firestore.internal.FirestoreImpl

class FirestoreModule {
    val firestore: Firestore by lazy {
        FirestoreImpl()
    }
}
