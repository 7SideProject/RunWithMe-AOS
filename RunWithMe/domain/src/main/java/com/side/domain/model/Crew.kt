package com.side.domain.model

data class Crew(
    val seq : Long,
    val name: String,
    val founder: String,
    val duration: String,
    val method: String,
    val often: String,
    val distance: String,
    val totalMemberCount : Int,
    val memberCount: Int,
    val cost: Int
)
