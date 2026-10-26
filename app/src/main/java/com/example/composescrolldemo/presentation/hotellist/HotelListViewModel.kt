package com.example.composescrolldemo.presentation.hotellist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composescrolldemo.domain.usecase.GetHotelsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for [HotelListScreen], implementing the MVI pattern.
 */
class HotelListViewModel(
    private val getHotelsUseCase: GetHotelsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotelListUiState())
    val uiState: StateFlow<HotelListUiState> = _uiState.asStateFlow()

    private val _effects = Channel<HotelListUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        loadHotels()
    }

    fun processIntent(intent: HotelListUiIntent) {
        when (intent) {
            is HotelListUiIntent.OnHotelClicked -> {
                viewModelScope.launch {
                    _effects.send(HotelListUiEffect.NavigateToDetail(intent.hotelId))
                }
            }

            HotelListUiIntent.Refresh -> {
                _uiState.update { it.copy(isLoading = true, error = null) }
                loadHotels()
            }
        }
    }

    private fun loadHotels() {
        viewModelScope.launch {
            getHotelsUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                }
                .collect { hotels ->
                    _uiState.update { it.copy(isLoading = false, hotels = hotels, error = null) }
                }
        }
    }
}