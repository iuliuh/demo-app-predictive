package com.autoencoder.glasdemoapp.main.userSchedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.autoencoder.glasdemoapp.models.*
import com.autoencoder.glasdemoapp.models.DefaultQuery.Companion.getQueryObjectFromEnum
import com.autoencoder.glasdemoapp.shared.base.BaseCommand
import com.autoencoder.glasdemoapp.shared.base.BaseViewModel
import com.autoencoder.glasdemoapp.shared.utils.extensions.convertToHour
import glas.ai.sdk.DataIOEngine
import glas.ai.sdk.GlasAI
import glas.ai.sdk.NotificationsEngine

class UserDailyScheduleViewModel : BaseViewModel() {

    private val _scheduleItems = MutableLiveData<List<UserDailyScheduleItem>>()
    val scheduleItem: LiveData<List<UserDailyScheduleItem>> = _scheduleItems

    private val responseType = object : TypeToken<Response<TimeData>>() {}.type

    fun getUserDailySchedule() {
        GlasAI.instance().apply {
            notificationsEngine().registerListener(notificationsListener)
            dataIO().registerListener(dataIOListener)
            dataIO().queryData(gson.toJson(DefaultQuery.LEAVE_HOME_TIME.getQueryObjectFromEnum()))
        }
    }

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
            val items = mutableListOf<UserDailyScheduleItem>()
            gson.fromJson<Response<TimeData>>(leaveHomeMockData, responseType)?.also {
                it.reply.data.timeData.forEach { timeData ->
                    items.add(
                        UserDailyScheduleItem(
                            Day.getDayFromString(
                                timeData.day
                            ),
                            timeData.probability,
                            timeData.time.convertToHour()
                        )
                    )
                }
            }
            gson.fromJson<Response<TimeData>>(arriveWorkMockData, responseType)?.also {
                it.reply.data.timeData.forEach { timeData ->
                    items.add(
                        UserDailyScheduleItem(
                            Day.getDayFromString(
                                timeData.day
                            ),
                            timeData.probability,
                            timeData.time.convertToHour(),
                            true
                        )
                    )
                }
            }
            _scheduleItems.postValue(items.groupBy { it.day }.values.flatten())
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

    private val leaveHomeMockData = "{\n" +
            "    \"api_info\": {\n" +
            "        \"type\": \"lite_machine_readable\",\n" +
            "        \"version\": \"1.2.0\"\n" +
            "    },\n" +
            "    \"message_id\": \"{bd47a3ac-e4bf-4d66-91e7-5e32ea11184c}\",\n" +
            "    \"reply\": {\n" +
            "        \"data\": {\n" +
            "            \"leave-home-time\": [\n" +
            "                {\n" +
            "                    \"day\": \"saturday\",\n" +
            "                    \"probability\": 0.2857142984867096,\n" +
            "                    \"time\": 41400000\n" +
            "                },\n" +
            "                {\n" +
            "                    \"day\": \"sunday\",\n" +
            "                    \"probability\": 0.2857142984867096,\n" +
            "                    \"time\": 64800000\n" +
            "                },\n" +
            "                {\n" +
            "                    \"day\": \"wednesday\",\n" +
            "                    \"probability\": 0.5714285969734192,\n" +
            "                    \"time\": 39600000\n" +
            "                },\n" +
            "                {\n" +
            "                    \"day\": \"thursday\",\n" +
            "                    \"probability\": 1,\n" +
            "                    \"time\": 32400000\n" +
            "                },\n" +
            "                {\n" +
            "                    \"day\": \"tuesday\",\n" +
            "                    \"probability\": 0.1428571492433548,\n" +
            "                    \"time\": 30600000\n" +
            "                },\n" +
            "                {\n" +
            "                    \"day\": \"friday\",\n" +
            "                    \"probability\": 0.5714285969734192,\n" +
            "                    \"time\": 34200000\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"query_id\": \"{9017cb53-a14e-4446-b7d9-8b4f60b11ef9}\"\n" +
            "    }\n" +
            "}"

    private val arriveWorkMockData = "{\n" +
            "    \"api_info\": {\n" +
            "        \"type\": \"lite_machine_readable\",\n" +
            "        \"version\": \"1.2.0\"\n" +
            "    },\n" +
            "    \"message_id\": \"{8c956075-9338-4d8f-85be-07f0729a6826}\",\n" +
            "    \"reply\": {\n" +
            "        \"data\": {\n" +
            "            \"arrive-work-time\": [\n" +
            "                {\n" +
            "                    \"day\": \"friday\",\n" +
            "                    \"probability\": 0.2222222238779068,\n" +
            "                    \"time\": 28800000\n" +
            "                },\n" +
            "                {\n" +
            "                    \"day\": \"monday\",\n" +
            "                    \"probability\": 0.2222222238779068,\n" +
            "                    \"time\": 30600000\n" +
            "                },\n" +
            "                {\n" +
            "                    \"day\": \"tuesday\",\n" +
            "                    \"probability\": 0.1111111119389534,\n" +
            "                    \"time\": 25200000\n" +
            "                },\n" +
            "                {\n" +
            "                    \"day\": \"saturday\",\n" +
            "                    \"probability\": 0.2222222238779068,\n" +
            "                    \"time\": 37800000\n" +
            "                },\n" +
            "                {\n" +
            "                    \"day\": \"sunday\",\n" +
            "                    \"probability\": 0.1111111119389534,\n" +
            "                    \"time\": 12600000\n" +
            "                },\n" +
            "                {\n" +
            "                    \"day\": \"wednesday\",\n" +
            "                    \"probability\": 0.3333333432674408,\n" +
            "                    \"time\": 32400000\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"query_id\": \"{f24fadc8-56f8-4b45-b539-096c1bb3e80c}\"\n" +
            "    }\n" +
            "}"

}