package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PointsOfInterestQuery(
    @SerializedName("query") val query: PointsOfInterestQueryData,
    @SerializedName("message_id") val messageId: String = "{90221a60-1a1a-434a-9e1b-db0c77374324}",
    @SerializedName("api_info") val apiInfo: ApiInfo = ApiInfo()
) : Parcelable

@Parcelize
data class PointsOfInterestQueryData(
    @SerializedName("data") val data: PointsOfInterestRequestArea,
    @SerializedName("type") val type: String = "get-P.O.I.s"
) : Parcelable

@Parcelize
data class PointsOfInterestRequestArea(
    @SerializedName("area") val area: PointsOfInterestRequestBox
) : Parcelable

@Parcelize
data class PointsOfInterestRequestBox(
    @SerializedName("center-location") val centerLocation: Location,
    @SerializedName("width") val width: Int = 1000,
    @SerializedName("height") val height: Int = 1000
): Parcelable