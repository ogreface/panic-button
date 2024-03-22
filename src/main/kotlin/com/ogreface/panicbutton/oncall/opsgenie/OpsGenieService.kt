package com.ogreface.panicbutton.oncall.opsgenie

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.ogreface.panicbutton.ApplicationProperties
import com.ogreface.panicbutton.OpsgenieConfig
import com.ogreface.panicbutton.SlackConfig
import com.ogreface.panicbutton.data.Person
import com.ogreface.panicbutton.data.Team
import com.ogreface.panicbutton.data.opsgenie.OnCallResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class OpsGenieService {
    // Create a service to use the opsgenie API and fetch a list of teams, and who is on call for them

    @Autowired
    var appProperties: ApplicationProperties? = null

    var webClientBuilder: WebClient.Builder? = null


    fun getTeams(): List<Team>? {
        //Use the opsgenie SDK to fetch the teams
        val webClient = WebClient.builder().baseUrl(appProperties?.opsgenie?.urL!!)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "GenieKey ${appProperties?.opsgenie?.apiKey!!}")
            .build()

        val response:String? = webClient.get()
            .uri("/schedules/on-calls?")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val mapper = ObjectMapper().registerKotlinModule()
        val onCallResponse: OnCallResponse? = response?.let { mapper.readValue(it) }

        val teamList = onCallResponse?.data?.map { teamData ->
            Team(teamData._parent.id, teamData._parent.name.removeSuffix("_schedule"), teamData.onCallParticipants.map {
                Person(it.id, it.name, it.name, it.type)
            })

        }

        return teamList

    }
}
