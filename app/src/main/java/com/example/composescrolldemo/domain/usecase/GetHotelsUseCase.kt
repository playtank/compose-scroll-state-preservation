package com.example.composescrolldemo.domain.usecase

import com.example.composescrolldemo.domain.model.Hotel
import com.example.composescrolldemo.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow

/**
 * Single-responsibility use-case that exposes the hotel list.
 *
 * Use-cases are the boundary between domain and presentation — they
 * contain the business rules (here: simply delegate to the repository,
 * but could add sorting, filtering, etc.).
 */
class GetHotelsUseCase(private val repository: HotelRepository) {

    /** Invoke operator so the use-case reads like a function call: `getHotels()`. */
    operator fun invoke(): Flow<List<Hotel>> = repository.getHotels()
}
