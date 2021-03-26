package com.autoencoder.glasdemoapp.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.autoencoder.glasdemoapp.R

enum class Service(@StringRes val title: Int, @DrawableRes val drawable: Int, val type: String) {
    HOME_LOCATION(R.string.home_location, R.drawable.home, "home-address"),
    WORK_LOCATION(R.string.work_location, R.drawable.work, "work-address"),
    POINTS_OF_INTEREST(R.string.points_of_interest, R.drawable.points_of_interest, "favourite-P.O.I."),
    HEADING_INFORMATION(R.string.heading_information, R.drawable.heading_information, "heading"),
    GAS_STATIONS(R.string.gas_station_brands, R.drawable.gas_station, "favourite-gasstation"),
    SUPERMARKETS(R.string.supermarket_brands, R.drawable.supermarket, "favourite-supermarket"),
    USER_DAILY_SCHEDULE(
        R.string.user_daily_schedule,
        R.drawable.user_daily_schedule,
        "leave-home-time"
    );

    companion object {
        fun getServiceFromType(type: String) = values().find { it.type == type }
    }
}