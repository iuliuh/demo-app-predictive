package com.autoencoder.glasdemoapp.main.headingInformation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.autoencoder.glasdemoapp.models.*
import com.autoencoder.glasdemoapp.shared.base.BaseCommand
import com.autoencoder.glasdemoapp.shared.base.BaseViewModel
import glas.ai.sdk.DataIOEngine
import glas.ai.sdk.GlasAI
import glas.ai.sdk.NotificationsEngine

private const val HEADING_SUB_TYPE = "heading"

class HeadingInformationViewModel : BaseViewModel() {

    private val _headingItems = MutableLiveData<List<HeadingInformationItem>>()
    val headingItems: LiveData<List<HeadingInformationItem>> = _headingItems

    private val _headingInformation = MutableLiveData<List<HeadingInformation>>()
    val headingInformation: LiveData<List<HeadingInformation>> = _headingInformation

    private lateinit var mockData: String

    private val responseType =
        object : TypeToken<NotificationResponse<HeadingInformation>>() {}.type

    fun requestHeadingInformation(responseData: String) {
//        GlasAI.instance().notificationsEngine().subscribe(HEADING_SUB_TYPE)
        mockData = responseData
        val result = gson.fromJson<NotificationResponse<HeadingInformation>>(
            responseData,
            responseType
        )
        with(result.notification.data) {
            setHeadingItems(this)
            _headingInformation.value = this
        }
    }

    private fun setHeadingItems(data: List<HeadingInformation>) {
        val headingItemsList = mutableListOf<HeadingInformationItem>()
        data.forEachIndexed { index, headingInformation ->
            val lastWaypointLocation =
                headingInformation.route.legs.firstOrNull()?.waypoints?.lastOrNull()?.location
            headingItemsList.add(
                HeadingInformationItem(
                    index,
                    lastWaypointLocation?.latitude ?: 0f,
                    lastWaypointLocation?.longitude ?: 0f,
                    headingInformation.tag
                )
            )
        }
        _headingItems.value = headingItemsList
    }

    private val notificationsListener = object : NotificationsEngine.Listener {

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

    private val dataIOListener = object : DataIOEngine.Listener {
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
            return
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
}