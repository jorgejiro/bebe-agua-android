package com.jjrapps.bebeagua.ui.changelog

import androidx.lifecycle.ViewModel
import com.jjrapps.bebeagua.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChangelogViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ChangelogUiState>(ChangelogUiState.Loading)
    val uiState: StateFlow<ChangelogUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = ChangelogUiState.Success(
            releases = ChangelogCatalog.releases.sortedByDescending { it.versionCode },
            currentVersionCode = BuildConfig.VERSION_CODE
        )
    }
}
