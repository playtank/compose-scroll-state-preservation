package com.example.composescrolldemo.presentation.hoteldetail

import com.example.composescrolldemo.domain.model.Hotel

data class HotelDetailUiState(
    val isLoading: Boolean = true,
    val hotel: Hotel? = null,
    val error: String? = null,
)

sealed interface HotelDetailUiIntent {

    data object NavigateBack : HotelDetailUiIntent
}

sealed interface HotelDetailUiEffect {

    data object PopBackStack : HotelDetailUiEffect
}
