package com.example.composescrolldemo.di

import com.example.composescrolldemo.data.repository.HotelRepositoryImpl
import com.example.composescrolldemo.domain.repository.HotelRepository
import com.example.composescrolldemo.domain.usecase.GetHotelByIdUseCase
import com.example.composescrolldemo.domain.usecase.GetHotelsUseCase

/**
 * Manual dependency injection container — a lightweight alternative to Hilt
 * that keeps the sample self-contained and easy to understand.
 *
 * In a production project you would replace this with a Hilt module or a
 * Koin module.  The important thing is that the ViewModel factories below
 * live *outside* the Composable tree so they don't get re-created on every
 * recomposition.
 */
object AppContainer {

    // ── Singletons ────────────────────────────────────────────────────────────

    private val hotelRepository: HotelRepository = HotelRepositoryImpl()

    // ── Use-cases (stateless — safe to recreate, but we reuse them too) ───────

    val getHotelsUseCase: GetHotelsUseCase = GetHotelsUseCase(hotelRepository)
    val getHotelByIdUseCase: GetHotelByIdUseCase = GetHotelByIdUseCase(hotelRepository)
}
