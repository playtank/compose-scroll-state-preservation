package com.example.composescrolldemo.domain.model

/**
 * Pure domain model — no Android or framework dependencies.
 *
 * @param id        Unique identifier (used as the navigation argument).
 * @param name      Display name shown on the list and detail screens.
 * @param location  City / neighbourhood string.
 * @param pricePerNight  Nightly rate in USD.
 * @param rating    Guest rating out of 5.0.
 * @param reviewCount  Number of guest reviews.
 * @param amenities Short list of highlight amenities.
 */
data class Hotel(
    val id: Int,
    val name: String,
    val location: String,
    val pricePerNight: Int,
    val rating: Float,
    val reviewCount: Int,
    val amenities: List<String>,
)
