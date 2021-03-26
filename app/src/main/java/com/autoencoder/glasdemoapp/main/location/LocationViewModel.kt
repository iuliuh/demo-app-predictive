package com.autoencoder.glasdemoapp.main.location

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.models.*
import com.autoencoder.glasdemoapp.shared.base.BaseCommand
import com.autoencoder.glasdemoapp.shared.base.BaseViewModel
import glas.ai.sdk.DataIOEngine
import glas.ai.sdk.GlasAI
import glas.ai.sdk.NotificationsEngine

enum class LocationScreen(@DrawableRes val icon: Int, @StringRes val title: Int) {
    HOME(R.drawable.home, R.string.home_location),
    WORK(R.drawable.work, R.string.work_location)
}

abstract class LocationViewModel(locationScreen: LocationScreen) : BaseViewModel() {

    val icon = MutableLiveData<Int>(locationScreen.icon)
    val title = MutableLiveData<Int>(locationScreen.title)
    val lat = MutableLiveData<Float>(0f)
    val lng = MutableLiveData<Float>(0f)

    private val _markerLocation = MutableLiveData<Location>()
    val markerLocation: LiveData<Location> = _markerLocation

    private val responseType = object : TypeToken<Response<Map<String, Location>>>() {}.type

    abstract fun requestMarkerLocation()

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
            val mockedLocation = when (locationScreen) {
                LocationScreen.HOME -> homeLocationMock
                LocationScreen.WORK -> workLocationMock
            }
            gson.fromJson<Response<Map<String, Location>>>(mockedLocation, responseType)?.also {
                it.reply.data.values.firstOrNull()?.let { location ->
                    lat.postValue(location.latitude)
                    lng.postValue(location.longitude)
                    _markerLocation.postValue(location)
                }
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

    private val homeLocationMock = "{\n" +
            "    \"api_info\": {\n" +
            "        \"type\": \"lite_machine_readable\",\n" +
            "        \"version\": \"1.2.0\"\n" +
            "    },\n" +
            "    \"message_id\": \"{dcfb829a-7b25-4f07-a1cb-b8135dce5512}\",\n" +
            "    \"reply\": {\n" +
            "        \"data\": {\n" +
            "            \"home-address\": {\n" +
            "                \"latitude\": 46.74379858333334,\n" +
            "                \"longitude\": 23.57008826999999\n" +
            "            }\n" +
            "        },\n" +
            "        \"query_id\": \"{aa1a8321-73ac-4426-864a-90a519e203ba}\"\n" +
            "    }\n" +
            "}"

    private val workLocationMock = "{\n" +
            "    \"api_info\": {\n" +
            "        \"type\": \"lite_machine_readable\",\n" +
            "        \"version\": \"1.2.0\"\n" +
            "    },\n" +
            "    \"message_id\": \"{f4c4b34b-f07c-4fb3-b038-9ffd9f1dd689}\",\n" +
            "    \"reply\": {\n" +
            "        \"data\": {\n" +
            "            \"work-address\": {\n" +
            "                \"latitude\": 46.75879699444444,\n" +
            "                \"longitude\": 23.60232783888889\n" +
            "            }\n" +
            "        },\n" +
            "        \"query_id\": \"{d9aeb344-7a04-4afd-a549-1354d558a159}\"\n" +
            "    }\n" +
            "}"
}