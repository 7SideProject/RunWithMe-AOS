package com.side.data.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// DataStore 확장 함수
object preferencesKeys {
    val JWT = stringPreferencesKey("jwt")
    val REFRESH_TOKEN = stringPreferencesKey("refresh-token")
    val ID = stringPreferencesKey("id")
    val SEQ = stringPreferencesKey("seq")
    val WEIGHT = intPreferencesKey("weight")
    val CHALLENG_SEQ = intPreferencesKey("challeng_seq")
    val GOAL_AMOUNT = longPreferencesKey("goal_amount")
    val GOAL_TYPE = stringPreferencesKey("goal_type")
}


enum class DATASTORE_KEY {
    TYPE_INT, TYPE_STRING, TYPE_BOOLEAN, TYPE_LONG
}
//const val DATASTORE_KEY_TYPE_INT = 0
//const val DATASTORE_KEY_TYPE_STRING = 1
//const val DATASTORE_KEY_TYPE_BOOLEAN = 2

suspend fun <T> DataStore<Preferences>.saveValue(key: Preferences.Key<T>, value: T) {
    edit { prefs -> prefs[key] = value }
}

// 암호화 적용
suspend fun DataStore<Preferences>.saveEncryptStringValue(key: Preferences.Key<String>, value: String) {
    edit { prefs -> prefs[key] = encrypt(value) }
}

suspend fun <T> DataStore<Preferences>.getValue(key: Preferences.Key<T>, type: DATASTORE_KEY): Flow<Any> {
    return data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[key] ?: when (type) {
                DATASTORE_KEY.TYPE_INT -> {
                    0
                }
                DATASTORE_KEY.TYPE_BOOLEAN -> {
                    false
                }
                DATASTORE_KEY.TYPE_STRING -> {
                    ""
                }
                DATASTORE_KEY.TYPE_LONG -> {
                    0L
                }
                else -> {}
            }
        }
}

// 암호화 적용
suspend fun DataStore<Preferences>.getDecryptStringValue(key: Preferences.Key<String>): Flow<Any> {
    return data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            decrypt(prefs[key] ?: "")
        }
}