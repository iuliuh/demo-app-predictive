package com.autoencoder.glasdemoapp.main.multipleLocations

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.models.Location
import com.autoencoder.glasdemoapp.models.PointOfInterest
import com.autoencoder.glasdemoapp.models.PointsOfInterestData
import com.autoencoder.glasdemoapp.models.Response
import com.autoencoder.glasdemoapp.shared.base.BaseCommand
import com.autoencoder.glasdemoapp.shared.base.BaseViewModel
import glas.ai.sdk.DataIOEngine
import glas.ai.sdk.GlasAI
import glas.ai.sdk.NotificationsEngine
import java.util.*

private const val DESCRIPTION_START_INDEX = 2

enum class MultipleLocationsScreen(@DrawableRes val icon: Int, @StringRes val title: Int) {
    GAS_STATIONS(R.drawable.gas_station, R.string.gas_station_brands),
    SUPERMARKETS(R.drawable.supermarket, R.string.supermarket_brands),
    POINTS_OF_INTEREST(R.drawable.points_of_interest, R.string.points_of_interest)
}

abstract class MultipleLocationsViewModel(multipleLocationScreen: MultipleLocationsScreen) :
    BaseViewModel() {

    protected val _markers = MutableLiveData<List<PointOfInterest>>()
    val markers: LiveData<List<PointOfInterest>> = _markers

    val icon = MutableLiveData<Int>(multipleLocationScreen.icon)
    val title = MutableLiveData<Int>(multipleLocationScreen.title)
    val description = MutableLiveData<String>("")

    private val responseType = object : TypeToken<Response<PointsOfInterestData>>() {}.type

    abstract fun requestMarkerLocation(location: Location)

    protected val notificationsListener = object : NotificationsEngine.Listener {

        override fun availableNotificationTypes(notificationTypes: List<String>) {
            return
        }

        override fun error(errorJson: String) {
            _baseCmd.postValue(BaseCommand.ShowToast(errorJson))
        }

        override fun existingSubscriptions(subscriptions: List<String>) {
            return
        }

        override fun notificationAvailable(notificationJson: String) {
            return
        }

        override fun success(successJson: String) {
            return
        }
    }

    protected val dataIOListener = object : DataIOEngine.Listener {
        override fun availableDataTimeWindow(timeWindowJson: String) {
            return
        }

        override fun availableDataTypes(metaDataJson: Map<Byte, String>) {
            return
        }

        override fun availableQueryTypes(queryTypes: List<String>) {
            return
        }

        override fun dataAvailable(dataJson: String) {
            val mockedLocation = when (multipleLocationScreen) {
                MultipleLocationsScreen.GAS_STATIONS -> gasStationsMockData
                MultipleLocationsScreen.SUPERMARKETS -> supermarketsMockData
                MultipleLocationsScreen.POINTS_OF_INTEREST -> pointsOfInterestsMockData
            }
            gson.fromJson<Response<PointsOfInterestData>>(mockedLocation, responseType)?.also {
                _markers.postValue(it.reply.data.pointsOfInterests)
                description.postValue(it.reply.data.pointsOfInterests.fold("") { description, element ->
                    "$description, ${element.name.capitalize(Locale.ROOT)}"
                }.substring(DESCRIPTION_START_INDEX))
            }
        }

        override fun error(errorJson: String) {
            _baseCmd.postValue(BaseCommand.ShowToast(errorJson))
        }

        override fun existingSubscriptions(subscriptionsJson: String) {
            return
        }

        override fun query(queryJson: String) {
            return
        }

        override fun success(successJson: String) {
            return
        }
    }

    fun unregisterListeners() {
        GlasAI.instance().apply {
            notificationsEngine().unregisterListener(notificationsListener)
            dataIO().unregisterListener(dataIOListener)
        }
    }

    private val supermarketsMockData = "{\n" +
            "    \"api_info\": {\n" +
            "        \"type\": \"lite_machine_readable\",\n" +
            "        \"version\": \"1.2.0\"\n" +
            "    },\n" +
            "    \"message_id\": \"{39c44973-23a2-4f96-b8a5-244726c84c46}\",\n" +
            "    \"push\": {\n" +
            "        \"query_id\": \"{90221a60-1a1a-434a-9e1b-db0c77374324}\",\n" +
            "        \"data\": {\n" +
            "            \"favourite-P.O.I.s\": [\n" +
            "                {\n" +
            "                    \"name\": \"lidl\",\n" +
            "                    \"type\": \"supermarket\",\n" +
            "                    \"latitude\": 46.756352935174185,\n" +
            "                    \"longitude\": 23.595342113286332\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"kaufland\",\n" +
            "                    \"type\": \"supermarket\",\n" +
            "                    \"latitude\": 46.759562573301665,\n" +
            "                    \"longitude\": 23.564520868681996\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    }\n" +
            "}"

    private val gasStationsMockData = "{\n" +
            "    \"api_info\": {\n" +
            "        \"type\": \"lite_machine_readable\",\n" +
            "        \"version\": \"1.2.0\"\n" +
            "    },\n" +
            "    \"message_id\": \"{39c44973-23a2-4f96-b8a5-244726c84c46}\",\n" +
            "    \"push\": {\n" +
            "        \"query_id\": \"{90221a60-1a1a-434a-9e1b-db0c77374324}\",\n" +
            "        \"data\": {\n" +
            "            \"favourite-P.O.I.s\": [\n" +
            "                {\n" +
            "                    \"name\": \"Rompetrol\",\n" +
            "                    \"type\": \"gasstation\",\n" +
            "                    \"latitude\": 46.756352935174185,\n" +
            "                    \"longitude\": 23.595342113286332\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"MOL\",\n" +
            "                    \"type\": \"gasstation\",\n" +
            "                    \"latitude\": 46.759562573301665,\n" +
            "                    \"longitude\": 23.564520868681996\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    }\n" +
            "}"

    private val pointsOfInterestsMockData = "{\n" +
            "    \"api_info\": {\n" +
            "        \"type\": \"lite_machine_readable\",\n" +
            "        \"version\": \"1.2.0\"\n" +
            "    },\n" +
            "    \"message_id\": \"{bc6934ad-3a9c-4058-917d-8eac657b0bb3}\",\n" +
            "    \"reply\": {\n" +
            "        \"data\": {\n" +
            "            \"favourite-P.O.I.s\": [\n" +
            "                {\n" +
            "                    \"latitude\": 46.769434800000006,\n" +
            "                    \"longitude\": 23.609950700000002,\n" +
            "                    \"name\": \"Mega Image\",\n" +
            "                    \"type\": \"supermarket\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"latitude\": 46.7950426,\n" +
            "                    \"longitude\": 23.613674200000002,\n" +
            "                    \"name\": \"Lukoil\",\n" +
            "                    \"type\": \"gasstation\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"query_id\": \"{26c60979-4172-4ad4-aea9-32264f4d0724}\"\n" +
            "    }\n" +
            "}"
}