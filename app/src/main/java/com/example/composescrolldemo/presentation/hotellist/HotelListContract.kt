package com.example.composescrolldemo.presentation.hotellist

import com.example.composescrolldemo.domain.model.Hotel

/**
 * MVI Contract for the Hotel List screen, with scroll state persistence.
 */
data class HotelListUiState(
    val isLoading: Boolean = true,
    val hotels: List<Hotel> = emptyList(),
    val error: String? = null,
    // Hoisted scroll state to survive back-stack destruction on navigation
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0,
)

sealed interface HotelListUiIntent {
    data class OnHotelClicked(val hotelId: Int) : HotelListUiIntent

    // Persists scroll position when list settles
    data class OnScrollPositionChanged(
        val firstVisibleItemIndex: Int,
        val firstVisibleItemScrollOffset: Int,
    ) : HotelListUiIntent

    data object Refresh : HotelListUiIntent
}

sealed interface HotelListUiEffect {
    data class NavigateToDetail(val hotelId: Int) : HotelListUiEffect
    data class ShowMessage(val message: String) : HotelListUiEffect
}