package com.example.composescrolldemo.data.repository

import com.example.composescrolldemo.data.source.HotelMockDataSource
import com.example.composescrolldemo.domain.model.Hotel
import com.example.composescrolldemo.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Concrete implementation of [HotelRepository] using [HotelMockDataSource].
 *
 * Uses Flow builder to allow seamless migration to remote/local data sources.
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
