package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.wada811.dependencyproperty.dependency
import io.github.droidkaigi.confsched2020.data.repository.di.RepositoryModule
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.SearchResult
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository

class SearchSessionsViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionRepository by dependency<RepositoryModule, SessionRepository> { it.sessionRepository }

    data class UiModel(val searchResult: SearchResult) {
        companion object {
            val EMPTY = UiModel(SearchResult.EMPTY)
        }
    }

    // LiveDatas
    private val sessionsLoadStateLiveData: LiveData<LoadState<SessionContents>> = liveData {
        emitSource(
            sessionRepository.sessionContents()
                .toLoadingState()
                .asLiveData()
        )
    }
    private val searchQueryLiveData: MutableLiveData<String> = MutableLiveData("")

    // Produce UiModel
    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = sessionsLoadStateLiveData,
        liveData2 = searchQueryLiveData
    ) { current: UiModel,
        sessionsLoadState: LoadState<SessionContents>,
        searchQuery: String
        ->

        val searchResult = when (sessionsLoadState) {
            is LoadState.Loaded -> {
                sessionsLoadState.value.search(searchQuery)
            }
            else -> {
                current.searchResult
            }
        }

        UiModel(
            searchResult = searchResult
        )
    }

    fun updateSearchQuery(query: String) {
        searchQueryLiveData.postValue(query)
    }
}
