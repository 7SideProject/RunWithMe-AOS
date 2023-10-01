package com.side.runwithme.util

import com.side.runwithme.BuildConfig



const val BASE_URL = BuildConfig.BASEURL


const val LOCATION_PERMISSION_REQUEST_CODE = 1000

const val DATASTORE_NAME = "preferences_datastore"

enum class SERVICE_ACTION {
    START, RESUME, STOP, PAUSE, FIRST_SHOW
}

/**
 * 타이머 갱신 주기
 */
const val TIMER_UPDATE_INTERVAL = 50L


/**
 * Tracking 옵션
 */
const val LOCATION_UPDATE_INTERVAL = 4000L
const val FASTEST_LOCATION_UPDATE_INTERVAL = 4000L

const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"
const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
const val NOTIFICATION_ID = 1 // 채널 ID는 0이면 안됨


/** 경로 표시 옵션 **/

class DRAWING_POLYLINE_FAST {
    companion object{
        const val SHORT = 150L
        const val MIDDLE = 70L
        const val LONG = 7L
    }
}

class GOAL_TYPE {
    companion object{
        const val TIME = 0
        const val DISTANCE = 1
    }

    
}
