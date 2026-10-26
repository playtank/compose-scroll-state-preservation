package com.example.composescrolldemo.data.source

import com.example.composescrolldemo.domain.model.Hotel

/**
 * In-memory mock data source that mimics an Expedia-style hotel catalogue.
 *
 * In a real project this would be replaced with a Retrofit service call or
 * a Room DAO — the repository pattern means nothing in the presentation or
 * domain layers needs to change when that happens.
 */
internal object HotelMockDataSource {

    val hotels: List<Hotel> = buildList {
        val names = listOf(
            "Grand Hyatt Seattle",
            "The Westin Bellevue",
            "Marriott Waterfront",
            "Hilton Garden Inn",
            "Sheraton Grand Hotel",
            "Four Seasons Resort",
            "Ritz-Carlton Downtown",
            "InterContinental Tower",
            "W Hotel & Residences",
            "Kimpton Palliser Hotel",
            "Loews Regency New York",
            "JW Marriott Chicago",
            "Andaz West Hollywood",
            "Rosewood Sand Hill",
            "Ace Hotel Brooklyn",
            "The Standard High Line",
            "1 Hotel Central Park",
            "NoMad Hotel Los Angeles",
            "Soho Grand Hotel",
            "The Greenwich Hotel",
            "Baccarat Hotel New York",
            "Aman New York",
            "Park Hyatt Vienna",
            "Mandarin Oriental Boston",
            "St. Regis San Francisco",
            "Palace Hotel San Francisco",
            "Langham Chicago",
            "Thompson Chicago",
            "Radisson Blu Aqua",
            "Viceroy Chicago",
            "Canopy by Hilton Seattle",
            "Hotel Indigo Nashville",
            "Renaissance Nashville",
            "Omni Hotel Nashville",
            "Hutton Hotel Nashville",
            "The Hermitage Hotel",
            "Hotel Preston Nashville",
            "Dream Nashville",
            "Virgin Hotels Nashville",
            "Graduate Nashville",
            "Hampton Inn Downtown Denver",
            "Hotel Teatro Denver",
            "The Brown Palace Hotel",
            "Halcyon Hotel Cherry Creek",
            "Hewing Hotel Minneapolis",
            "Canopy Minneapolis Mill District",
            "The Westin Minneapolis",
            "Loews Minneapolis Hotel",
            "Kimpton Hotel Palomar Dallas",
            "Autograph Collection Dallas",
        )
        val locations = listOf(
            "Seattle, WA", "Bellevue, WA", "Seattle Waterfront", "Downtown Seattle",
            "Midtown Manhattan", "Beverly Hills, CA", "Downtown Chicago", "Houston, TX",
            "West Hollywood, CA", "Calgary, AB", "Upper East Side, NY", "Chicago Loop",
            "West Hollywood, CA", "Menlo Park, CA", "Williamsburg, NY", "Meatpacking District, NY",
            "Central Park South, NY", "Downtown LA", "SoHo, NY", "Tribeca, NY",
            "Midtown West, NY", "Upper East Side, NY", "Vienna, Austria", "Back Bay, Boston",
            "Union Square, SF", "Union Square, SF", "River North, Chicago", "Thompson Center, Chicago",
            "Lakeshore East, Chicago", "Gold Coast, Chicago", "South Lake Union, Seattle",
            "The Gulch, Nashville", "Midtown Nashville", "Downtown Nashville", "Music Row, Nashville",
            "Downtown Nashville", "Opryland, Nashville", "SoBro, Nashville", "SoBro, Nashville",
            "Vanderbilt Area, Nashville", "LoDo, Denver", "Theater District, Denver",
            "Downtown Denver", "Cherry Creek, Denver", "North Loop, Minneapolis",
            "Mill District, Minneapolis", "Downtown Minneapolis", "Nicollet Mall, Minneapolis",
            "Uptown Dallas", "Deep Ellum, Dallas",
        )
        val amenitiesList = listOf(
            listOf("Pool", "Spa", "Gym", "Restaurant"),
            listOf("Free Wi-Fi", "Gym", "Business Center"),
            listOf("Ocean View", "Restaurant", "Bar"),
            listOf("Free Parking", "Pet-Friendly", "Gym"),
            listOf("Rooftop Bar", "Spa", "Valet Parking"),
        )

        repeat(50) { index ->
            add(
                Hotel(
                    id = index + 1,
                    name = names[index % names.size],
                    location = locations[index % locations.size],
                    pricePerNight = 120 + (index * 17) % 480,   // range $120–$599
                    rating = 3.5f + (index % 15) * 0.1f,         // range 3.5–5.0
                    reviewCount = 200 + (index * 43) % 3800,
                    amenities = amenitiesList[index % amenitiesList.size],
                )
            )
        }
    }
}
