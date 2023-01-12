package com.side.runwithme.util

import com.side.runwithme.BuildConfig

const val API_KEY = BuildConfig.kakaoApiKey

const val ACTION_START_OR_RESUME_SERVICE = "action_start_or_resume_service"
const val ACTION_STOP_SERVICE = "action_stop_service"

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
