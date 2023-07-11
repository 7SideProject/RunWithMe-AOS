package com.side.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import okhttp3.MultipartBody

class TypeConverter {

    @TypeConverter
    fun multipartToJson(value : MultipartBody.Part) : String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToMultiPart(value :  String?) : MultipartBody.Part? {
        return Gson().fromJson(value,MultipartBody.Part::class.java)
    }

}