package io.github.droidkaigi.confsched2020.data.repository.di

import android.content.Context
import com.wada811.dependencyproperty.DependencyModule
import io.github.droidkaigi.confsched2020.data.api.di.ApiModule
import io.github.droidkaigi.confsched2020.data.db.AnnouncementDatabase
import io.github.droidkaigi.confsched2020.data.db.ContributorDatabase
import io.github.droidkaigi.confsched2020.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2020.data.db.di.DbModule
import io.github.droidkaigi.confsched2020.data.firestore.di.FirestoreModule
import io.github.droidkaigi.confsched2020.data.repository.internal.DataAnnouncementRepository
import io.github.droidkaigi.confsched2020.data.repository.internal.DataContributorRepository
import io.github.droidkaigi.confsched2020.data.repository.internal.DataSessionRepository
import io.github.droidkaigi.confsched2020.data.repository.internal.DataSponsorRepository
import io.github.droidkaigi.confsched2020.data.repository.internal.workmanager.FavoriteToggleWork
import io.github.droidkaigi.confsched2020.model.repository.AnnouncementRepository
import io.github.droidkaigi.confsched2020.model.repository.ContributorRepository
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.model.repository.SponsorRepository

class RepositoryModule(
    private val context: Context,
    val apiModule: ApiModule = ApiModule(),
    val dbModule: DbModule = DbModule(context),
    private val firestoreModule: FirestoreModule = FirestoreModule()
) : DependencyModule {
    val sessionRepository: SessionRepository by lazy {
        DataSessionRepository(
            apiModule.droidKaigiApi,
            apiModule.googleFormApi,
            dbModule.sessionDatabase,
            firestoreModule.firestore,
            FavoriteToggleWork(context)
        )
    }

    val sponsorRepository: SponsorRepository by lazy {
        DataSponsorRepository(
            apiModule.droidKaigiApi,
            dbModule.sessionDatabase as SponsorDatabase
        )
    }

    val announcementRepository: AnnouncementRepository by lazy {
        DataAnnouncementRepository(
            apiModule.droidKaigiApi,
            dbModule.sessionDatabase as AnnouncementDatabase
        )
    }

    val contributorRepository: ContributorRepository by lazy {
        DataContributorRepository(
            apiModule.droidKaigiApi,
            dbModule.sessionDatabase as ContributorDatabase
        )
    }
}
