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
 *
 * ┌──────────────────────────────────────────────────────────────────────┐
 * │  UiState  ──► StateFlow<HotelListUiState>  (observed by Composable)  │
 * │  UiIntent ◄── processIntent()              (called by Composable)    │
 * │  UiEffect ──► Channel<HotelListUiEffect>   (collected once by UI)    │
 * └──────────────────────────────────────────────────────────────────────┘
 *
 * SCROLL POSITION SURVIVAL
 * ────────────────────────
 * The ViewModel is scoped to the NavBackStackEntry for the list destination.
 * Android keeps the ViewModel alive as long as the back-stack entry exists —
 * which includes the time the user is on the detail screen.  Therefore any
 * state stored here is automatically available when the user presses Back.
 *
 * The Composable mirrors `LazyListState` ↔ ViewModel using:
 *   1. `LazyListState(initialFirstVisibleItemIndex, initialFirstVisibleItemScrollOffset)`
 *      to seed the correct position on (re-)composition.
 *   2. A `LaunchedEffect` that observes `LazyListState.firstVisibleItemIndex` /
 *      `firstVisibleItemScrollOffset` and dispatches [HotelListUiIntent.OnScrollPositionChanged]
 *      back to this ViewModel on every change.
 */
class HotelListViewModel(
    private val getHotelsUseCase: GetHotelsUseCase,
) : ViewModel() {

    // ── UiState ──────────────────────────────────────────────────────────────
    private val _uiState = MutableStateFlow(HotelListUiState())
    val uiState: StateFlow<HotelListUiState> = _uiState.asStateFlow()

    // ── UiEffect (one-shot events) ────────────────────────────────────────────
    // Channel with BUFFERED capacity so effects are never dropped if the
    // collector hasn't started yet (e.g., while navigating).
    private val _effects = Channel<HotelListUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        loadHotels()
    }

    // ── Intent dispatcher ─────────────────────────────────────────────────────

    fun processIntent(intent: HotelListUiIntent) {
        when (intent) {
            is HotelListUiIntent.OnHotelClicked -> onHotelClicked(intent.hotelId)

            // THE FIX: persist the LazyColumn's live scroll position into UiState.
            // This is the single line that makes scroll preservation work:
            //   • When the user scrolls, this intent arrives here.
            //   • UiState is updated with the new index + offset.
            //   • On Back navigation, HotelListScreen re-reads these values from
            //     uiState and passes them to LazyListState's constructor, so the
            //     list reappears at exactly the right position.
            is HotelListUiIntent.OnScrollPositionChanged -> {
                _uiState.update { state ->
                    state.copy(
                        firstVisibleItemIndex = intent.firstVisibleItemIndex,
                        firstVisibleItemScrollOffset = intent.firstVisibleItemScrollOffset,
                    )
                }
            }

            HotelListUiIntent.Refresh -> {
                _uiState.update { it.copy(isLoading = true, error = null) }
                loadHotels()
            }
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun loadHotels() {
        viewModelScope.launch {
            getHotelsUseCase()
                .catch { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = throwable.message ?: "Unknown error",
                        )
                    }
                }
                .collect { hotels ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            hotels = hotels,
                            error = null,
                        )
                    }
                }
        }
    }

    private fun onHotelClicked(hotelId: Int) {
        viewModelScope.launch {
            _effects.send(HotelListUiEffect.NavigateToDetail(hotelId))
        }
    }
}
