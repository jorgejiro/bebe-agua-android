package com.jjrapps.bebeagua.ui.changelog

sealed interface ChangelogUiState {
    data object Loading : ChangelogUiState
    data class Success(
        val releases: List<ChangelogRelease>,
        val currentVersionCode: Int
    ) : ChangelogUiState
    data class Error(val message: String) : ChangelogUiState
}
