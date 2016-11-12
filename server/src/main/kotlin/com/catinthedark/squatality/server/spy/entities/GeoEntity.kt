package com.catinthedark.squatality.server.spy.entities

data class GeoEntity(
    val status: String? = "",
    val country: String? = "",
    val countryCode: String? = "",
    val region: String? = "",
    val regionName: String? = "",
    val city: String? = "",
    val zip: String? = "",
    val lat: Double? = 0.0,
    val lon: Double? = 0.0,
    val timezone: String? = "",
    val isp: String? = "",
    val org: String? = "",
    val `as`: String? = "",
    val query: String? = ""
)
