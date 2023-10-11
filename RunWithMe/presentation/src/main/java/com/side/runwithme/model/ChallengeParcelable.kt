package com.side.runwithme.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.lang.Math.round

@Parcelize
data class ChallengeParcelable(
    val seq : Long,
    val managerSeq: Long,
    val managerName: String,
    val name: String,
    val description: String,
    val imgSeq: Long,
    val goalDays: Int,
    val goalType: String,
    val goalAmount: Long,
    val dateStart : String,
    val dateEnd: String,
    val nowMember: Int,
    val maxMember: Int,
    val cost: Int,
    val password: String? = null
) : Parcelable {

    fun getGoalTypeNaming() : String =
        if(goalType == "distance"){
            "거리"
        }else {
            "시간"
        }

    fun getGoalAmountWithUnit() : String =
        if(goalType == "distance"){
            "${goalAmount / 1000}km"
        }else {
            "${goalAmount / 60}분"
        }
}