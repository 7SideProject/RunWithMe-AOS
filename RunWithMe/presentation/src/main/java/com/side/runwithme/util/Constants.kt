package com.side.runwithme.util

import com.side.runwithme.BuildConfig

const val API_KEY = BuildConfig.kakaoApiKey

const val ACTION_START_SERVICE = "0"
const val ACTION_PAUSE_SERVICE = "1"
const val ACTION_RESUME_SERVICE = "2"
const val ACTION_STOP_SERVICE = "3"

const val LOCATION_UPDATE_INTERVAL = 5000L
const val FASTEST_LOCATION_UPDATE_INTERVAL = 5000L