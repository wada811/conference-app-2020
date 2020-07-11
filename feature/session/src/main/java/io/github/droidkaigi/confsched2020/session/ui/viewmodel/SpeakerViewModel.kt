package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.wada811.dependencyproperty.dependency
import io.github.droidkaigi.confsched2020.data.repository.di.RepositoryModule
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.model.SpeakerId
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.session.ui.SpeakerFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class SpeakerViewModel(application: Application) : AndroidViewModel(application) {
    private val speakerId by dependency<SpeakerFragment.SpeakerFragmentArgsModule, SpeakerId> { it.navArgs.speakerId }
    private val searchQuery by dependency<SpeakerFragment.SpeakerFragmentArgsModule, String?> { it.navArgs.searchQuery }
    private val sessionRepository by dependency<RepositoryModule, SessionRepository> { it.sessionRepository }

    // UiModel definition
    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val speaker: Speaker?,
        val sessions: List<SpeechSession>,
        val searchQuery: String?
    ) {
        companion object {
            val EMPTY = UiModel(false, null, null, listOf(), null)
        }
    }

    // LiveDatas
    private val speakerLoadStateLiveData: LiveData<LoadState<Speaker>> = liveData {
        sessionRepository.sessionContents()
            .map { it.speakers.first { speaker -> speakerId == speaker.id } }
            .toLoadingState()
            .collect { loadState: LoadState<Speaker> ->
                emit(loadState)
            }
    }

    private val speakerSessionLoadingStateLiveData = liveData {
        sessionRepository.sessionContents()
            .map {
                it.sessions
                    .filterIsInstance<SpeechSession>()
                    .filter { session ->
                        session.speakers.firstOrNull { speaker ->
                            speakerId == speaker.id
                        } != null
                    }
            }
            .toLoadingState()
            .collect { loadState: LoadState<List<SpeechSession>> ->
                emit(loadState)
            }
    }

    // Produce UiModel
    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = speakerLoadStateLiveData,
        liveData2 = speakerSessionLoadingStateLiveData
    ) { current: UiModel,
        speakerLoadState: LoadState<Speaker>,
        speakerSessionLoadState: LoadState<List<SpeechSession>> ->
        val isLoading = speakerLoadState.isLoading || speakerSessionLoadState.isLoading
        val speaker = when (speakerLoadState) {
            is LoadState.Loaded -> {
                speakerLoadState.value
            }
            else -> {
                current.speaker
            }
        }
        val speakerSessions = when (speakerSessionLoadState) {
            is LoadState.Loaded -> {
                speakerSessionLoadState.value
            }
            else -> {
                current.sessions
            }
        }
        UiModel(
            isLoading = isLoading,
            error = (
                speakerLoadState.getErrorIfExists()
                    ?: speakerSessionLoadState.getErrorIfExists()
                ).toAppError(),
            speaker = speaker,
            sessions = speakerSessions,
            searchQuery = searchQuery
        )
    }
}
