package com.example.composescrolldemo.navigation

/**
 * Typesafe navigation route definitions.
 *
 * Using sealed class + companion object constants keeps routes co-located
 * and makes refactoring safe (no magic strings scattered across the codebase).
 */
sealed class NavRoutes(val route: String) {

    /** Hotel list — root destination. */
    data object HotelList : NavRoutes("hotel_list")

    /**
     * Hotel detail — receives the hotel ID as a required path argument.
     *
     * Full route pattern: "hotel_detail/{hotelId}"
     * Navigation call:    navController.navigate("hotel_detail/$id")
     */
    data object HotelDetail : NavRoutes("hotel_detail/{hotelId}") {

        fun createRoute(hotelId: Int) = "hotel_detail/$hotelId"

        const val ARG_HOTEL_ID = "hotelId"
    }
}
