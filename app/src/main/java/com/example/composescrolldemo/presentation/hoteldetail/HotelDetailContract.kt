package com.example.composescrolldemo.presentation.hoteldetail

import com.example.composescrolldemo.domain.model.Hotel

// ─────────────────────────── UiState ─────────────────────────────────────────

data class HotelDetailUiState(
    val isLoading: Boolean = true,
    val hotel: Hotel? = null,
    val error: String? = null,
)

// ─────────────────────────── UiIntent ────────────────────────────────────────

sealed interface HotelDetailUiIntent {

    /** User pressed the system/toolbar back button. */
    data object NavigateBack : HotelDetailUiIntent
}

// ─────────────────────────── UiEffect ────────────────────────────────────────

sealed interface HotelDetailUiEffect {

    /** Pop this destination off the back-stack. */
    data object PopBackStack : HotelDetailUiEffect
}
