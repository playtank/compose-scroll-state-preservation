package com.example.composescrolldemo.navigation

/**
 * Typesafe navigation route definitions.
 */
sealed class NavRoutes(val route: String) {

    /** Hotel list — root destination. */
    data object HotelList : NavRoutes("hotel_list")

    /**
     * Hotel detail — receives the hotel ID as a required path argument.
     */
    data object HotelDetail : NavRoutes("hotel_detail/{hotelId}") {

        fun createRoute(hotelId: Int) = "hotel_detail/$hotelId"

        const val ARG_HOTEL_ID = "hotelId"
    }
}
