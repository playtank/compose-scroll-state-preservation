package com.example.composescrolldemo.presentation.hoteldetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composescrolldemo.domain.usecase.GetHotelByIdUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for [HotelDetailScreen].
 *
 * Receives the [hotelId] from the nav argument and loads the matching [Hotel].
 */
class HotelDetailViewModel(
    private val hotelId: Int,
    private val getHotelByIdUseCase: GetHotelByIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotelDetailUiState())
    val uiState: StateFlow<HotelDetailUiState> = _uiState.asStateFlow()

    private val _effects = Channel<HotelDetailUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        loadHotel()
    }

    fun processIntent(intent: HotelDetailUiIntent) {
        when (intent) {
            HotelDetailUiIntent.NavigateBack -> {
                viewModelScope.launch {
                    _effects.send(HotelDetailUiEffect.PopBackStack)
                }
            }
        }
    }

    private fun loadHotel() {
        viewModelScope.launch {
            getHotelByIdUseCase(hotelId)
                .catch { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
                .collect { hotel ->
                    _uiState.update { it.copy(isLoading = false, hotel = hotel) }
                }
        }
    }
}
