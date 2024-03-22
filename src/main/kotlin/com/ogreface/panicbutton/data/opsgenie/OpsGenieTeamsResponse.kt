package com.ogreface.panicbutton.data.opsgenie

import com.fasterxml.jackson.annotation.JsonProperty
import com.ogreface.panicbutton.data.Person
import com.ogreface.panicbutton.data.Team

data class OnCallParticipant(
    val id: String,
    val name: String,
    val type: String
)

data class Parent(
    val id: String,
    val name: String,
    val enabled: Boolean
)

data class TeamData(
    val _parent: Parent,
    val onCallParticipants: List<OnCallParticipant>
)

data class OnCallResponse(
    val data: List<TeamData>,
    val took: Double,
    val requestId: String
)

