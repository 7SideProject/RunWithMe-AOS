package com.side.runwithme.view.challenge.create

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.runwithme.util.GOAL_TYPE
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import com.side.runwithme.view.join.JoinViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import okhttp3.MultipartBody
import java.lang.Math.round
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

    private val _goalType : MutableStateFlow<String> = MutableStateFlow(GOAL_TYPE.TIME.type)
    val goalType get() = _goalType.asStateFlow()

    private val _goalAmount : MutableStateFlow<String> = MutableStateFlow("30")
    val goalAmount get() = _goalAmount.asStateFlow()

    private val goalDistanceAmount : MutableStateFlow<Int> = MutableStateFlow(30)
    private val goalTimeAmount : MutableStateFlow<Int> = MutableStateFlow(3)

    private val _goalDays : MutableStateFlow<String> = MutableStateFlow("3")
    val goalDays get() = _goalDays.asStateFlow()

    private val _maxMember: MutableStateFlow<String> = MutableStateFlow("7")
    val maxMember get() = _maxMember.asStateFlow()

    private val _cost : MutableStateFlow<String> = MutableStateFlow("500")
    val cost get() = _cost.asStateFlow()

    private val _password: MutableStateFlow<String?> = MutableStateFlow(null)
    val password get() = _password.asStateFlow()


    private val _createEventFlow = MutableEventFlow<Event>()
    val createEventFlow get() = _createEventFlow.asEventFlow()

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

    fun setGoalType(type: GOAL_TYPE){
        _goalType.value = type.type
    }


    fun setGoalDistance(amount: Int){
        goalDistanceAmount.value = amount

        setGoalAmountByDistance()
    }

    fun setGoalTime(amount: Int){
        goalTimeAmount.value = amount

        setGoalAmountByTime()
    }

    fun setGoalAmountByDistance(){
        _goalAmount.value = "${goalDistanceAmount.value}.0"
    }

    fun setGoalAmountByTime(){
        _goalAmount.value = goalTimeAmount.value.toString()
    }

    fun setDays(days: Int){
        _goalDays.value = days.toString()
    }

    fun setMaxMember(max: String){
        _maxMember.value = max
    }

    fun setCost(cost: String){
        _cost.value = cost
    }

    fun setPassword(password: String?){
        _password.value = password
    }

    fun create(){
        // amount는 type에 따라 km -> m , 분 -> 초로 변경
    }


    fun refresh(){
        challengeImg.value = null
        challengeImgMultiPart.value = null
        challengeName.value = ""
        challengeDescription.value = ""
        _goalWeeks.value = 4
        _goalType.value = GOAL_TYPE.TIME.type
        _goalAmount.value = "30"
        goalDistanceAmount.value = 30
        goalTimeAmount.value = 3
        _goalDays.value = "3"
        _maxMember.value = "7"
        _cost.value = "500"
        _password.value = null
    }


    sealed class Event {
        data class Success(val message: String) : Event()
        data class Fail(val message: String) : Event()

        class Error(): Event()
    }

}