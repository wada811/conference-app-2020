package io.github.droidkaigi.confsched2020.data.repository.internal.workmanager

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wada811.dependencyproperty.dependency
import io.github.droidkaigi.confsched2020.data.repository.di.RepositoryModule
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import kotlinx.coroutines.withTimeout

internal class FavoriteToggleWorker(
    appContext: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val sessionRepository by (applicationContext as Application).dependency<RepositoryModule, SessionRepository> { it.sessionRepository }
    override suspend fun doWork(): Result {
        if (runAttemptCount > 0) {
            return Result.failure()
        }
        val id = workerParams.inputData.getString(INPUT_SESSION_ID_KEY)
        id ?: return Result.failure()
        withTimeout(3000) {
            sessionRepository.toggleFavorite(SessionId(id))
        }
        return Result.success()
    }

    companion object {
        const val INPUT_SESSION_ID_KEY = "INPUT_SESSION_ID_KEY"
    }
}
