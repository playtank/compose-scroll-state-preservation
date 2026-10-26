package com.example.composescrolldemo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.composescrolldemo.presentation.hoteldetail.HotelDetailViewModel
import com.example.composescrolldemo.presentation.hotellist.HotelListViewModel

/**
 * Generic [ViewModelProvider.Factory] that accepts a lambda producing a ViewModel.
 *
 * Usage:
 * ```
 * val vm: HotelListViewModel = viewModel(
 *     factory = viewModelFactory { HotelListViewModel(AppContainer.getHotelsUseCase) }
 * )
 * ```
 */
class ViewModelFactory<VM : ViewModel>(
    private val creator: () -> VM,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = creator() as T
}

/** Convenience top-level helper so call-sites read naturally. */
fun <VM : ViewModel> viewModelFactory(creator: () -> VM): ViewModelFactory<VM> =
    ViewModelFactory(creator)

// ── Concrete factories (avoids repeating lambda syntax at call-sites) ─────────

fun hotelListViewModelFactory(): ViewModelFactory<HotelListViewModel> =
    viewModelFactory { HotelListViewModel(AppContainer.getHotelsUseCase) }

fun hotelDetailViewModelFactory(hotelId: Int): ViewModelFactory<HotelDetailViewModel> =
    viewModelFactory { HotelDetailViewModel(hotelId, AppContainer.getHotelByIdUseCase) }
