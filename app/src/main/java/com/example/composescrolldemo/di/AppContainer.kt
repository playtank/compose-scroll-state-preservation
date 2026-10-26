package com.example.composescrolldemo.di

import com.example.composescrolldemo.data.repository.HotelRepositoryImpl
import com.example.composescrolldemo.domain.repository.HotelRepository
import com.example.composescrolldemo.domain.usecase.GetHotelByIdUseCase
import com.example.composescrolldemo.domain.usecase.GetHotelsUseCase

/**
 * Manual dependency injection container — a lightweight alternative to Hilt.
 * In a production project, this would be replaced with Hilt or Koin.
 */
object AppContainer {

    // Singletons
    private val hotelRepository: HotelRepository = HotelRepositoryImpl()

    // Use cases
    val getHotelsUseCase: GetHotelsUseCase = GetHotelsUseCase(hotelRepository)
    val getHotelByIdUseCase: GetHotelByIdUseCase = GetHotelByIdUseCase(hotelRepository)
}
