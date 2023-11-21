package com.side.runwithme.util

import com.side.runwithme.BuildConfig



const val BASE_URL = BuildConfig.BASEURL
const val CHALLENGE = "challenge"
const val USER = "users"
const val GET_CHALLNEGE_IMG = "challenge-image"
const val GET_PROFILE_IMG = "profile-image"

const val LOCATION_PERMISSION_REQUEST_CODE = 1000

const val DATASTORE_NAME = "preferences_datastore"

enum class RUNNING_STATE(val message: String) {
    START("러닝을 시작합니다."), RESUME("러닝을 다시 시작합니다."), STOP("러닝이 종료되었습니다."), PAUSE("러닝이 일시중지 되었습니다."), FIRST_SHOW("")
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


enum class DRAWING_POLYLINE_FAST(val time: Long) {
    SHORT(150L), MIDDLE(70L), LONG(7L)
}

enum class GOAL_TYPE(val apiName: String) {
    TIME("time"), DISTANCE("distance")
}


// Challenge 상태 (챌린지 시작, 시작 안했지만 가입 되어있음, 시작 안했지만 가입 안함, 챌린지 끝남)
enum class CHALLENGE_STATE {
    START, NOT_START_AND_ALEADY_JOIN, NOT_START_AND_NOT_JOIN, END, NOTHING
}

