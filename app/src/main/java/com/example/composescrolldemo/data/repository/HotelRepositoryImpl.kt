package com.example.composescrolldemo.data.repository

import com.example.composescrolldemo.data.source.HotelMockDataSource
import com.example.composescrolldemo.domain.model.Hotel
import com.example.composescrolldemo.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Concrete implementation of [HotelRepository] that pulls data from the
 * in-memory [HotelMockDataSource].
 *
 * The `flow { }` builder makes it trivial to swap this for a suspend
 * network/database call without touching any other layer.
 */
class HotelRepositoryImpl : HotelRepository {

    override fun getHotels(): Flow<List<Hotel>> = flow {
        // Simulate a brief IO delay so the loading state is visible.
        kotlinx.coroutines.delay(300)
        emit(HotelMockDataSource.hotels)
    }

    override fun getHotelById(id: Int): Flow<Hotel?> = flow {
        kotlinx.coroutines.delay(150)
        emit(HotelMockDataSource.hotels.find { it.id == id })
    }
}
