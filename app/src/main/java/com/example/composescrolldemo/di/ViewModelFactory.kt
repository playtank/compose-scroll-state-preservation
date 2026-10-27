package com.example.composescrolldemo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.composescrolldemo.presentation.hoteldetail.HotelDetailViewModel
import com.example.composescrolldemo.presentation.hotellist.HotelListViewModel
import com.example.composescrolldemo.presentation.recomposition.RecompositionViewModel
/**
 * Generic [ViewModelProvider.Factory] that accepts a lambda producing a ViewModel.
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

// Concrete factories
fun hotelListViewModelFactory(): ViewModelFactory<HotelListViewModel> =
    viewModelFactory { HotelListViewModel(AppContainer.getHotelsUseCase) }

fun hotelDetailViewModelFactory(hotelId: Int): ViewModelFactory<HotelDetailViewModel> =
    viewModelFactory { HotelDetailViewModel(hotelId, AppContainer.getHotelByIdUseCase) }

fun recompositionViewModelFactory(): ViewModelFactory<RecompositionViewModel> =
    viewModelFactory { RecompositionViewModel() }
