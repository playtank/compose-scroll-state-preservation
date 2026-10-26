package com.example.composescrolldemo.presentation.hotellist

import com.example.composescrolldemo.domain.model.Hotel

/**
 * MVI contract for the Hotel List screen.
 *
 * ┌─────────┐  UiIntent  ┌─────────────┐  UiState  ┌──────────┐
 * │  Screen │ ─────────► │  ViewModel  │ ─────────► │  Screen  │
 * └─────────┘            └──────┬──────┘            └──────────┘
 *                               │ UiEffect (one-shot)
 *                               ▼
 *                          SharedFlow
 *
 * Grouping all three types in one file makes diffs and reviews easier.
 */

// ─────────────────────────── UiState ─────────────────────────────────────────

/**
 * Immutable snapshot of everything the screen needs to render itself.
 *
 * @param isLoading          True while the hotel list is being fetched.
 * @param hotels             The fetched list (empty until loaded).
 * @param error              Human-readable error message, or null.
 * @param firstVisibleItemIndex       ← THE FIX: scroll position persisted here.
 * @param firstVisibleItemScrollOffset ← THE FIX: pixel offset within the item.
 *
 * WHY WE HOIST SCROLL STATE INTO THE VIEWMODEL
 * ─────────────────────────────────────────────
 * By default `rememberLazyListState()` is remembered inside the Composable's
 * own remembered scope.  When Compose Navigation pops a destination off the
 * back-stack it destroys that Composable entirely; when the user presses Back
 * the Composable is re-created from scratch, so `rememberLazyListState()`
 * returns a fresh state anchored at index 0 — the scroll position is LOST.
 *
 * The fix: store `firstVisibleItemIndex` and `firstVisibleItemScrollOffset`
 * inside the ViewModel (which survives navigation).  On each re-composition
 * we seed the `LazyListState` with the persisted values, and on every scroll
 * event we save them back.  The ViewModel lives as long as the NavGraph entry
 * is on the back-stack, so the values survive the round-trip to the detail
 * screen and back.
 */
data class HotelListUiState(
    val isLoading: Boolean = true,
    val hotels: List<Hotel> = emptyList(),
    val error: String? = null,

    // ── Scroll preservation fields ──────────────────────────────────────────
    // These two fields are the core of the "scroll position fix".
    // They mirror `LazyListState.firstVisibleItemIndex` and
    // `LazyListState.firstVisibleItemScrollOffset`.
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0,
)

// ─────────────────────────── UiIntent ────────────────────────────────────────

/**
 * All actions that can originate from the UI and flow into the ViewModel.
 *
 * Sealed classes make the exhaustive `when` expression at the call-site safe.
 */
sealed interface HotelListUiIntent {

    /** User tapped a hotel card. */
    data class OnHotelClicked(val hotelId: Int) : HotelListUiIntent

    /**
     * THE FIX — emitted by the LazyColumn's `rememberUpdatedState` / snapshot
     * listener every time the list settles on a new scroll position.
     *
     * BUG PATH (without this intent):
     *   Navigate away → back-stack pops Composable → LazyListState discarded
     *   → re-entry creates fresh LazyListState(0, 0) → list jumps to top.
     *
     * FIX PATH (with this intent):
     *   Every scroll → ViewModel persists index+offset in UiState
     *   → Navigate away → back → new LazyListState seeded from ViewModel values
     *   → list restores exact position.
     */
    data class OnScrollPositionChanged(
        val firstVisibleItemIndex: Int,
        val firstVisibleItemScrollOffset: Int,
    ) : HotelListUiIntent

    /** Pull-to-refresh or explicit retry after an error. */
    data object Refresh : HotelListUiIntent
}

// ─────────────────────────── UiEffect ────────────────────────────────────────

/**
 * One-shot side-effects that the ViewModel emits to the UI layer.
 * Effects are consumed exactly once (via SharedFlow with replay = 0).
 */
sealed interface HotelListUiEffect {

    /** Tell the nav host to push the detail screen for [hotelId]. */
    data class NavigateToDetail(val hotelId: Int) : HotelListUiEffect

    /** Show a transient snackbar / toast message. */
    data class ShowMessage(val message: String) : HotelListUiEffect
}
