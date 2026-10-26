package com.example.composescrolldemo.domain.usecase

import com.example.composescrolldemo.domain.model.Hotel
import com.example.composescrolldemo.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow

/**
 * Single-responsibility use-case that resolves a hotel by its [id].
 */
class GetHotelByIdUseCase(private val repository: HotelRepository) {

    operator fun invoke(id: Int): Flow<Hotel?> = repository.getHotelById(id)
}
