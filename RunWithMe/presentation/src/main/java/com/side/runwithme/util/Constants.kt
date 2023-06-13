package com.side.runwithme.util

import com.side.runwithme.BuildConfig

//const val API_KEY = BuildConfig.kakaoApiKey
//
//const val KAKAO_REST_API_KEY = BuildConfig.kakaoRestApiKey
//
//const val REDIRECT_URL = BuildConfig.redirectURL

const val BASE_URL = BuildConfig.baseURL

const val LOCATION_PERMISSION_REQUEST_CODE = 1000

const val DATASTORE_NAME = "preferences_datastore"

const val KEY_INT = 0
const val KEY_STRING = 1
const val KEY_BOOLEAN = 2

const val ACTION_START_OR_RESUME_SERVICE = "action_start_or_resume_service"
const val ACTION_STOP_SERVICE = "action_stop_service"
const val ACTION_PAUSE_SERVICE = "action_pause_service"
const val ACTION_SHOW_RUNNING_ACTIVITY = "ACTION_SHOW_RUNNING_ACTIVITY"

/**
 * 목표 타입
 */
const val GOAL_TYPE_TIME = "time"
const val GOAL_TYPE_DISTANCE = "distance"

/**
 * Tracking 옵션
 */
const val LOCATION_UPDATE_INTERVAL = 5000L
const val FASTEST_LOCATION_UPDATE_INTERVAL = 5000L

const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"
const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
const val NOTIFICATION_ID = 1 // 채널 ID는 0이면 안됨
