package com.example.composescrolldemo.presentation.hotellist

import com.example.composescrolldemo.domain.model.Hotel

/**
 * MVI Contract for the Hotel List screen.
 */
data class HotelListUiState(
    val isLoading: Boolean = true,
    val hotels: List<Hotel> = emptyList(),
    val error: String? = null,
)

sealed interface HotelListUiIntent {
    data class OnHotelClicked(val hotelId: Int) : HotelListUiIntent

    data object Refresh : HotelListUiIntent
}

sealed interface HotelListUiEffect {
    data class NavigateToDetail(val hotelId: Int) : HotelListUiEffect
    data class ShowMessage(val message: String) : HotelListUiEffect
}