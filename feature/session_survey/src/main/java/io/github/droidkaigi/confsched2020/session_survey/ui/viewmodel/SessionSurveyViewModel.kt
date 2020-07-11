package io.github.droidkaigi.confsched2020.session_survey.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.wada811.dependencyproperty.dependency
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.session_survey.ui.SessionSurveyFragment

class SessionSurveyViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionId by dependency<SessionSurveyFragment.SessionSurveyFragmentArgsModule, SessionId> { it.navArgs.sessionId }

    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?
    ) {
        companion object {
            val EMPTY = UiModel(false, null)
        }
    }
}
