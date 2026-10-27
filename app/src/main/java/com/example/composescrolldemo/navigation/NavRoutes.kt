package com.example.composescrolldemo.navigation

/**
 * Typesafe navigation route definitions.
 */
sealed class NavRoutes(val route: String) {

    /** App home screen — root destination listing all demo features. */
    data object Home : NavRoutes("home")

    /** Hotel list — entry point of the Compose Scroll demo. */
    data object HotelList : NavRoutes("hotel_list")

    /**
     * Hotel detail — receives the hotel ID as a required path argument.
     */
    data object HotelDetail : NavRoutes("hotel_detail/{hotelId}") {

        fun createRoute(hotelId: Int) = "hotel_detail/$hotelId"

        const val ARG_HOTEL_ID = "hotelId"
    }
}
