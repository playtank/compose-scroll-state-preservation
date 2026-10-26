package com.example.composescrolldemo.domain.repository

import com.example.composescrolldemo.domain.model.Hotel
import kotlinx.coroutines.flow.Flow

/**
 * Domain-layer repository contract.
 *
 * Defined here (domain layer) so that use-cases depend only on this
 * abstraction and never on the concrete data-layer implementation.
 */
interface HotelRepository {

    /** Returns a cold [Flow] that emits the full hotel catalogue once. */
    fun getHotels(): Flow<List<Hotel>>

    /**
     * Returns a cold [Flow] that emits a single [Hotel] identified by [id],
     * or `null` when not found.
     */
    fun getHotelById(id: Int): Flow<Hotel?>
}
