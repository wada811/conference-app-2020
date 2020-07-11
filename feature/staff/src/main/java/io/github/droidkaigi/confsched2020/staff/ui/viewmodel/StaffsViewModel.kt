package io.github.droidkaigi.confsched2020.staff.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.wada811.dependencyproperty.dependency
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.StaffContents
import io.github.droidkaigi.confsched2020.staff.ui.StaffModule
import kotlinx.coroutines.FlowPreview

@FlowPreview
class StaffsViewModel(application: Application) : AndroidViewModel(application) {
    private val store: Store<Unit, StaffContents> by dependency<StaffModule, Store<Unit, StaffContents>> { it.staffsContentsStore }

    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val staffContents: StaffContents
    ) {
        companion object {
            val EMPTY = UiModel(false, null, StaffContents.EMPTY)
        }
    }

    // "stream" returns data along with the loading status.
    private val staffContentsLoadState: LiveData<StoreResponse<StaffContents>> =
        store.stream(StoreRequest.cached(key = Unit, refresh = true)).asLiveData()

    // "get" and "fetch" gets data directly, so you need to express the loading state yourself.
//    private val staffContents: LiveData<StaffContents> = liveData { store.get(Unit) }

    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = staffContentsLoadState
    ) { _, loadState ->
        val staffContents = when (loadState) {
            is StoreResponse.Data -> {
                loadState.value
            }
            else -> {
                StaffContents.EMPTY
            }
        }
        UiModel(
            isLoading = loadState is StoreResponse.Loading,
            error = loadState.errorOrNull().toAppError(),
            staffContents = staffContents
        )
    }
}
