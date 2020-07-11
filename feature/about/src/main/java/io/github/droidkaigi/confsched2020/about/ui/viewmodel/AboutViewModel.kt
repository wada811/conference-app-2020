package io.github.droidkaigi.confsched2020.about.ui.viewmodel

import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2020.model.AppError

class AboutViewModel : ViewModel() {

    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?
    ) {
        companion object {
            val EMPTY = UiModel(false, null)
        }
    }
}
