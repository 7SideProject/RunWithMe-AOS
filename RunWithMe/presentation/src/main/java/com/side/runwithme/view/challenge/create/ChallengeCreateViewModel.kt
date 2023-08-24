package com.side.runwithme.view.challenge.create

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MultipartBody
import java.time.LocalDate
import java.util.Calendar
import java.util.StringTokenizer

//@HiltViewModel
class ChallengeCreateViewModel(

): ViewModel(){

    val challengeImg : MutableStateFlow<Uri?> = MutableStateFlow(null)
    val challengeImgMultiPart : MutableStateFlow<MultipartBody.Part?> = MutableStateFlow(null)

    val challengeName : MutableStateFlow<String> = MutableStateFlow("")

    val challengeDescription : MutableStateFlow<String> = MutableStateFlow("")

    private val _goalWeeks : MutableStateFlow<Int> = MutableStateFlow(1)
    val goalWeeks get() = _goalWeeks.asStateFlow()

    private val _dateStart : MutableStateFlow<String> = MutableStateFlow("")
    val dateStart get() = _dateStart.asStateFlow()

    private val _dateEnd : MutableStateFlow<String> = MutableStateFlow("")
    val dateEnd get() = _dateEnd.asStateFlow()



    fun setDateStart(year: Int, month: Int, day : Int) {

        val fullMonth = if(month < 10) "0" + month else month.toString()
        val fullDay = if(day < 10) "0" + day else day.toString()

        _dateStart.value = "$year-$fullMonth-$fullDay"

        setDateEnd()
    }

    fun setDateEnd(){
        val tokenizer = StringTokenizer(dateStart.value, "-")
        val year = tokenizer.nextToken().toInt()
        val month = tokenizer.nextToken().toInt()
        val day = tokenizer.nextToken().toInt()


        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.add(Calendar.DAY_OF_MONTH, 7 * goalWeeks.value - 1)

        val endYear = calendar.get(Calendar.YEAR)
        val endMonth = calendar.get(Calendar.MONTH) + 1
        val endDay = calendar.get(Calendar.DAY_OF_MONTH)


        val fullMonth = if(endMonth < 10) "0" + endMonth else endMonth.toString()
        val fullDay = if(endDay < 10) "0" + endDay else endDay.toString()

        _dateEnd.value = "$endYear-$fullMonth-$fullDay"
    }

    fun initDate() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)

        val startDateYearInt = calendar.get(Calendar.YEAR)
        val startDateMonthInt = calendar.get(Calendar.MONTH) + 1
        val startDateDayInt = calendar.get(Calendar.DAY_OF_MONTH)

        setDateStart(startDateYearInt, startDateMonthInt, startDateDayInt)
    }

    fun setWeeks(weeks: Int){
        _goalWeeks.value = weeks

        setDateEnd()
    }


    fun refresh(){

    }
}