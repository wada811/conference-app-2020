package io.github.droidkaigi.confsched2020.announcement.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.wada811.dependencyproperty.dependency
import io.github.droidkaigi.confsched2020.data.repository.di.RepositoryModule
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.dropWhileIndexed
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.Announcement
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.model.repository.AnnouncementRepository
import timber.log.Timber
import timber.log.debug

class AnnouncementViewModel(application: Application) : AndroidViewModel(application) {
    private val announcementRepository by dependency<RepositoryModule, AnnouncementRepository> { it.announcementRepository }

    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val announcements: List<Announcement>,
        val isEmpty: Boolean,
        val expandedItemIds: Set<Long>
    ) {
        companion object {
            val EMPTY = UiModel(false, null, listOf(), false, setOf())
        }
    }

    private val languageLiveData = MutableLiveData(defaultLang())
    private val announcementLoadStateLiveData = languageLiveData
        .distinctUntilChanged()
        .switchMap {
            liveData<LoadState<List<Announcement>>> {
                emitSource(
                    announcementRepository.announcements()
                        // Because the empty list is returned
                        // when the initial refresh is not finished yet.
                        .dropWhileIndexed { index, value ->
                            index == 0 && value.isEmpty()
                        }
                        .toLoadingState()
                        .asLiveData()
                )
                try {
                    announcementRepository.refresh()
                } catch (e: Exception) {
                    // We can show announcements with cache
                    Timber.debug(e) { "Fail announcementRepository.refresh()" }
                }
            }
        }
    private val expandedItemIds = mutableSetOf<Long>()

    val uiModel = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = announcementLoadStateLiveData
    ) { _, loadState ->
        val announcements = (loadState as? LoadState.Loaded)?.value.orEmpty()
        UiModel(
            isLoading = loadState.isLoading,
            error = loadState.getErrorIfExists().toAppError(),
            announcements = announcements,
            isEmpty = !loadState.isLoading && announcements.isEmpty(),
            expandedItemIds = expandedItemIds
        )
    }

    fun loadLanguageSetting() {
        languageLiveData.value = defaultLang()
    }

    fun expandItem(id: Long) {
        expandedItemIds.add(id)
    }
}
