package com.autoencoder.glasdemoapp.main.list

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.models.*
import com.autoencoder.glasdemoapp.models.Service.*
import com.autoencoder.glasdemoapp.shared.base.BaseCommand
import com.autoencoder.glasdemoapp.shared.base.BaseViewModel
import com.autoencoder.glasdemoapp.shared.utils.LiveEvent
import glas.ai.sdk.DataIOEngine
import glas.ai.sdk.GlasAI
import glas.ai.sdk.GlasListener
import glas.ai.sdk.NotificationsEngine
import kotlin.concurrent.fixedRateTimer

private const val FULL_PERCENTAGE = 100
private const val TIMER_PERIOD = 30_000L
private const val TIMER_NAME = "DATA_REFRESH_TIMER"

class ListViewModel : BaseViewModel() {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val responseType = object : TypeToken<Response<List<ServiceAvailability>>>() {}.type

    private val _activities = MutableLiveData<List<DemoActivityItem>>()
    val activities: LiveData<List<DemoActivityItem>> = _activities

    fun onActivityClicked(activityItem: DemoActivityItem) {
//        if (activityItem.percentage < FULL_PERCENTAGE) {
//            displayNotReadyDialog(activityItem)
//            return
//        }
        _baseCmd.value = BaseCommand.Navigate(
            when (activityItem.service.title) {
                R.string.user_daily_schedule -> ListFragmentDirections.actionListFragmentToUserDailyScheduleFragment()
                R.string.heading_information -> ListFragmentDirections.actionListFragmentToHeadingInformationFragment()
                R.string.points_of_interest -> ListFragmentDirections.actionListFragmentToPointsOfInterestFragment()
                R.string.gas_station_brands -> ListFragmentDirections.actionListFragmentToGasStationsFragment()
                R.string.supermarket_brands -> ListFragmentDirections.actionListFragmentToSupermarketsFragment()
                R.string.work_location -> ListFragmentDirections.actionListFragmentToWorkLocationFragment()
                R.string.home_location -> ListFragmentDirections.actionListFragmentToHomeLocationFragment()
                else -> ListFragmentDirections.actionListFragmentToUserDailyScheduleFragment()
            }
        )
    }

    fun bootGlas() {
        GlasAI.instance().apply {
            notificationsEngine().registerListener(notificationsListener)
            dataIO().registerListener(dataIOListener)
            registerGlasListener(glasListener)
            boot()
        }
        loadActivities(serviceAvailability)
    }

    private val notificationsListener = object : NotificationsEngine.Listener {
        override fun availableNotificationTypes(notificationTypes: List<String>) {
            return
        }

        override fun error(errorJson: String) {
            return
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
//            metaDataJson[DataIOEngine.OUTPUT]?.let { loadActivities(it) }
        }

        override fun availableQueryTypes(queryTypes: List<String>) {
            return
        }

        override fun dataAvailable(dataJson: String) {
            // result of the query (work / home location)
            Log.d("ListViewModel", "dataAvailable: ${dataJson}")
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

    private val glasListener = object : GlasListener {
        override fun onReady() {
//            fixedRateTimer(TIMER_NAME, true, period = TIMER_PERIOD) {
//                GlasAI.instance().dataIO().queryAvailableDataTypes(DataIOEngine.INPUT_OUTPUT)
//            }
        }
    }

    private fun loadActivities(responseString: String) {
        val result = gson.fromJson<Response<List<ServiceAvailability>>>(
            responseString,
            responseType
        )
        val unsortedActivities = result.reply.data.mapNotNull { service ->
            Service.getServiceFromType(service.type)?.let { serviceType ->
                DemoActivityItem(
                    serviceType,
                    service.availability?.toIntOrNull() ?: 0
                )
            }
        }
        sortActivities(unsortedActivities)
    }

    private fun sortActivities(unsortedActivities: List<DemoActivityItem>) {
        val sortedActivities = mutableListOf<DemoActivityItem?>()
        sortedActivities.add(unsortedActivities.find { it.service == HOME_LOCATION })
        sortedActivities.add(unsortedActivities.find { it.service == WORK_LOCATION })
        sortedActivities.add(unsortedActivities.find { it.service == HEADING_INFORMATION })
        sortedActivities.add(unsortedActivities.find { it.service == POINTS_OF_INTEREST })
        sortedActivities.add(unsortedActivities.find { it.service == GAS_STATIONS })
        sortedActivities.add(unsortedActivities.find { it.service == SUPERMARKETS })
        sortedActivities.add(unsortedActivities.find { it.service == USER_DAILY_SCHEDULE })
        _activities.postValue(sortedActivities.filterNotNull())
    }

    private fun displayNotReadyDialog(activityItem: DemoActivityItem) {
        /** Add the remaining branches in the when clause after the descriptions have been added to the strings.xml file **/
        val description = when (activityItem.service) {
            HOME_LOCATION -> R.string.dialog_description_placeholder
            else -> R.string.dialog_description_placeholder
        }
        _cmd.value = Command.ShowDialog(
            activityItem.service.drawable,
            activityItem.service.title,
            description,
            activityItem.percentage
        )
    }

    fun unregisterListeners() {
        GlasAI.instance().apply {
            notificationsEngine().unregisterListener(notificationsListener)
            dataIO().unregisterListener(dataIOListener)
        }
    }

    private val serviceAvailability = "{\n" +
            "    \"api_info\": {\n" +
            "        \"type\": \"lite_machine_readable\",\n" +
            "        \"version\": \"1.2.0\"\n" +
            "    },\n" +
            "    \"message_id\": \"{6f6ec905-3ad2-4bf2-912d-8788376b772a}\",\n" +
            "    \"reply\": {\n" +
            "        \"data\": [\n" +
            "            {\n" +
            "                \"availability\": 100,\n" +
            "                \"state\": \"processed\",\n" +
            "                \"type\": \"arrive-work-time\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"availability\": 100,\n" +
            "                \"type\": \"favourite-P.O.I.\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"state\": \"processed\",\n" +
            "                \"type\": \"favourite-gasstation\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"state\": \"processed\",\n" +
            "                \"type\": \"favourite-supermarket\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"availability\": 72,\n" +
            "                \"type\": \"heading\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"availability\": 33,\n" +
            "                \"state\": \"processed\",\n" +
            "                \"type\": \"home-address\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"availability\": 95,\n" +
            "                \"state\": \"processed\",\n" +
            "                \"type\": \"leave-home-time\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"availability\": 100,\n" +
            "                \"state\": \"processed\",\n" +
            "                \"type\": \"work-address\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}"


    sealed class Command {
        data class ShowDialog(
            @DrawableRes val icon: Int,
            @StringRes val title: Int,
            @StringRes val description: Int,
            val percentage: Int
        ) : Command()
    }
}
