package com.side.runwithme.view.challenge.create

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.model.Challenge
import com.side.domain.usecase.challenge.CreateChallengeUseCase
import com.side.domain.utils.onError
import com.side.domain.utils.onFailure
import com.side.domain.utils.onSuccess
import com.side.runwithme.util.GOAL_TYPE
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.Calendar
import java.util.StringTokenizer
import javax.inject.Inject

@HiltViewModel
class ChallengeCreateViewModel @Inject constructor(
    private val createChallengeUseCase: CreateChallengeUseCase
): ViewModel(){

    // 화면에 binding 하기 위한 Img 객체 (Step2에서 Step1으로 이동해도 이미지가 살아있음)
    val challengeImg : MutableStateFlow<Uri?> = MutableStateFlow(null)
    // 실제 API로 요청하는 MultipartBody.Part
    val challengeImgMultiPart : MutableStateFlow<MultipartBody.Part?> = MutableStateFlow(null)

    val challengeName : MutableStateFlow<String> = MutableStateFlow("")

    val challengeDescription : MutableStateFlow<String> = MutableStateFlow("")

    private val _goalWeeks : MutableStateFlow<Int> = MutableStateFlow(1)
    val goalWeeks = _goalWeeks.asStateFlow()

    val dateStart : MutableStateFlow<String> = MutableStateFlow("")

    private val _dateEnd : MutableStateFlow<String> = MutableStateFlow("")
    val dateEnd = _dateEnd.asStateFlow()

    val goalType = MutableStateFlow(GOAL_TYPE.TIME)

    private val _goalAmount : MutableStateFlow<String> = MutableStateFlow("30")
    val goalAmount = _goalAmount.asStateFlow()

    private val goalDistanceAmount : MutableStateFlow<Int> = MutableStateFlow(3)
    private val goalTimeAmount : MutableStateFlow<Int> = MutableStateFlow(30)

    private val _goalDays : MutableStateFlow<String> = MutableStateFlow("3")
    val goalDays = _goalDays.asStateFlow()

    private val _maxMember: MutableStateFlow<String> = MutableStateFlow("7")
    val maxMember = _maxMember.asStateFlow()

    private val _cost : MutableStateFlow<String> = MutableStateFlow("500")
    val cost = _cost.asStateFlow()

    private val _password: MutableStateFlow<String?> = MutableStateFlow(null)
    val password = _password.asStateFlow()

    val isPasswordChallenge: MutableStateFlow<Boolean> = MutableStateFlow(false)


    // EventFlow

    private val _createEventFlow = MutableEventFlow<Event>()
    val createEventFlow = _createEventFlow.asEventFlow()

    private val _dialogGoalWeeksEventFlow = MutableEventFlow<Event>()
    val dialogGoalWeeksEventFlow = _dialogGoalWeeksEventFlow.asEventFlow()

    private val _dialogGoalDistanceEventFlow = MutableEventFlow<Event>()
    val dialogGoalDistanceEventFlow = _dialogGoalDistanceEventFlow.asEventFlow()

    private val _dialogGoalTimeEventFlow = MutableEventFlow<Event>()
    val dialogGoalTimeEventFlow = _dialogGoalTimeEventFlow.asEventFlow()

    private val _dialogGoalDaysEventFlow = MutableEventFlow<Event>()
    val dialogGoalDaysEventFlow = _dialogGoalDaysEventFlow.asEventFlow()

    private val _dialogMaxMemberEventFlow = MutableEventFlow<Event>()
    val dialogMaxMemberEventFlow = _dialogMaxMemberEventFlow.asEventFlow()

    private val _dialogCostEventFlow = MutableEventFlow<Event>()
    val dialogCostEventFlow = _dialogCostEventFlow.asEventFlow()

    private val _dialogPasswordEventFlow = MutableEventFlow<Event>()
    val dialogPasswordEventFlow = _dialogPasswordEventFlow.asEventFlow()

    fun setDateStart(year: Int, month: Int, day : Int) {

        val fullMonth = if(month < 10) "0" + month else month.toString()
        val fullDay = if(day < 10) "0" + day else day.toString()

        dateStart.value = "$year-$fullMonth-$fullDay"

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


    fun onClickGoalWeeksDialog(weeks: Int){
        _goalWeeks.value = weeks
        setDateEnd()

        viewModelScope.launch {
            _dialogGoalWeeksEventFlow.emit(Event.Success(""))
        }
    }

    fun setGoalType(type: GOAL_TYPE){
        goalType.value = type
    }


    fun onClickGoalDistance(amount: Int){
        goalDistanceAmount.value = amount

        setGoalAmountByDistance()

        viewModelScope.launch {
            _dialogGoalDistanceEventFlow.emit(Event.Success(""))
        }
    }

    fun onClickGoalTime(amount: Int){
        goalTimeAmount.value = (amount + 1) * 10

        setGoalAmountByTime()

        viewModelScope.launch {
            _dialogGoalTimeEventFlow.emit(Event.Success(""))
        }
    }


    fun setGoalAmountByDistance(){
        _goalAmount.value = "${goalDistanceAmount.value}"
    }

    fun setGoalAmountByTime(){
        _goalAmount.value = goalTimeAmount.value.toString()
    }

    fun onClickGoalDaysDialog(day: Int){
        _goalDays.value = day.toString()

        viewModelScope.launch {
            _dialogGoalDaysEventFlow.emit(Event.Success(""))
        }
    }

    fun onClickMaxMemberDialog(max: Int){
        _maxMember.value = max.toString()

        viewModelScope.launch {
            _dialogMaxMemberEventFlow.emit(Event.Success(""))
        }
    }

    fun onClickCostDialog(cost: Int){
        _cost.value = ((cost + 1) * 500).toString()

        viewModelScope.launch {
            _dialogCostEventFlow.emit(Event.Success(""))
        }
    }

    fun onClickPassWordDialog(password: String?){

        if(password.isNullOrBlank()){
            failPasswordEvent()
            return
        }

        if(password.length < 4){
            failPasswordEvent()
            return
        }

        _password.value = password
        viewModelScope.launch {
            _dialogPasswordEventFlow.emit(Event.Success(""))
        }
    }

    private fun failPasswordEvent(){
        viewModelScope.launch {
            _dialogPasswordEventFlow.emit(Event.Fail("비밀번호는 공백없이 4자리를 입력해주세요."))
        }
    }



    fun create(){
        // amount는 type에 따라 km -> m , 분 -> 초로 변경

        if(!isPasswordChallenge.value){
            _password.value = null
        }

        val goalType = if(this.goalType.value == GOAL_TYPE.TIME) "time" else "distance"


        val goalAmount = if(this.goalType.value == GOAL_TYPE.TIME) {
            this.goalAmount.value.toLong() * 60
        } else {
            this.goalAmount.value.toLong() * 1000
        }

        val challenge = Challenge(
            seq = 0,
            managerSeq = 0,
            name = challengeName.value,
            description = challengeDescription.value,
            imgSeq = 0,
            goalDays = goalDays.value.toInt(),
            goalType = goalType,
            goalAmount = goalAmount,
            dateStart = dateStart.value,
            dateEnd = dateEnd.value,
            maxMember = maxMember.value.toInt(),
            cost = cost.value.toInt(),
            password = password.value
        )


        viewModelScope.launch(Dispatchers.IO) {
            createChallengeUseCase(challenge, challengeImgMultiPart.value).collectLatest {
                it.onSuccess {
                    _createEventFlow.emit(Event.Success(it.message))
                }.onFailure {
                    _createEventFlow.emit(Event.Fail(it.message))
                }.onError {
                    Log.e("test123", "Create Challenge Error : ${it.message}  , ${it.cause}")
                }
            }
        }
    }



    sealed class Event {
        data class Success(val message: String) : Event()
        data class Fail(val message: String) : Event()

        class Error(): Event()
    }

}