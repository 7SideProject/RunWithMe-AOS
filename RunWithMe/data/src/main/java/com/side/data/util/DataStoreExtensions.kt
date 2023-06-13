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
    val EMAIL = stringPreferencesKey("email")
    val SEQ = stringPreferencesKey("seq")
    val WEIGHT = intPreferencesKey("weight")
}

const val KEY_INT = 0
const val KEY_STRING = 1
const val KEY_BOOLEAN = 2

suspend fun <T> DataStore<Preferences>.saveValue(key: Preferences.Key<T>, value: T) {
    edit { prefs -> prefs[key] = value }
}

// 암호화 적용
suspend fun DataStore<Preferences>.saveEncryptStringValue(key: Preferences.Key<String>, value: String) {
    edit { prefs -> prefs[key] = encrypt(value) }
}

suspend fun <T> DataStore<Preferences>.getValue(key: Preferences.Key<T>, type: Int): Flow<Any> {
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
                KEY_INT -> {
                    0
                }
                KEY_BOOLEAN -> {
                    false
                }
                KEY_STRING -> {
                    ""
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