package com.ogreface.panicbutton.oncall.opsgenie//import com.fasterxml.jackson.databind.ObjectMapper
//import com.fasterxml.jackson.module.kotlin.registerKotlinModule
//import com.ogreface.panicbutton.oncall.opsgenie.OpsGenieService
//import com.ogreface.panicbutton.data.Person
//import com.ogreface.panicbutton.data.Team
//import com.ogreface.panicbutton.data.opsgenie.OnCallResponse
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Assertions.assertNotNull
//import org.junit.jupiter.api.Assertions.assertNull
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito.`when`
//import org.mockito.Mockito.mock
//import org.springframework.test.util.ReflectionTestUtils
//import org.springframework.web.reactive.function.client.WebClient
//import reactor.core.publisher.Mono
//import org.springframework.http.HttpHeaders
//import org.springframework.web.reactive.function.client.WebClient.ResponseSpec
//
//class OpsGenieServiceTesFromChatGPT {
//
//    @Test
//    fun `test getTeams when response is valid`() {
//        // Mocking WebClient.Builder
//        val webClientBuilder = mock(WebClient.Builder::class.java)
//        val webClient = mock(WebClient::class.java)
//        val responseSpec = mock(ResponseSpec::class.java)
//        /* On this second iteration (after I put DI in the actual service, the tests changed. Unfortunately, it's still not using correct interfaces.
//          val webClient = mock(WebClient::class.java)
//         Thus this doesn't help much still.
//         */
//        `when`(webClientBuilder.baseUrl("https://api.opsgenie.com/v2")).thenReturn(webClientBuilder)
//        `when`(webClientBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, "GenieKey 51c0787a-7fd9-46b6-bb57-e2958aa64acd")).thenReturn(webClientBuilder)
//        `when`(webClientBuilder.build()).thenReturn(webClient)
//        `when`(webClient.get()).thenReturn(responseSpec)
//        `when`(responseSpec.uri("/schedules/on-calls?")).thenReturn(responseSpec)
//        `when`(responseSpec.retrieve()).thenReturn(responseSpec)
//        `when`(responseSpec.bodyToMono(String::class.java)).thenReturn(
//            Mono.just(
//                "{\"data\":[{\"_parent\":{\"id\":\"team_id\",\"name\":\"Team Name\"}," +
//                        "\"onCallParticipants\":[{\"id\":\"person_id\",\"name\":\"Person Name\",\"type\":\"USER\"}]}]}"
//            )
//        )
//
//        // Setting up OpsGenieService
//        val opsGenieService = OpsGenieService()
//        ReflectionTestUtils.setField(opsGenieService, "opsgenieApiKey", "dummyApiKey")
//        ReflectionTestUtils.setField(opsGenieService, "opsgenieUrl", "dummyUrl")
//        ReflectionTestUtils.setField(opsGenieService, "webClientBuilder", webClientBuilder)
//
//        // Test getTeams method
//        val teamList = opsGenieService.getTeams()
//
//        assertNotNull(teamList)
//        assertEquals(1, teamList?.size)
//        assertEquals("team_id", teamList?.get(0)?.id)
//        assertEquals("Team Name", teamList?.get(0)?.name)
//        assertEquals(1, teamList?.get(0)?.members?.size)
//        assertEquals("person_id", teamList?.get(0)?.members?.get(0)?.id)
//        assertEquals("Person Name", teamList?.get(0)?.members?.get(0)?.name)
//        assertEquals("USER", teamList?.get(0)?.members?.get(0)?.type)
//    }
//
//    @Test
//    fun `test getTeams when response is null`() {
//        // Mocking WebClient.Builder
//        val webClientBuilder = mock(WebClient.Builder::class.java)
//        val webClient = mock(WebClient::class.java)
//        val responseSpec = mock(ResponseSpec::class.java)
//
//        `when`(webClientBuilder.baseUrl("https://api.opsgenie.com/v2")).thenReturn(webClientBuilder)
//        `when`(webClientBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, "GenieKey 51c0787a-7fd9-46b6-bb57-e2958aa64acd")).thenReturn(webClientBuilder)
//        `when`(webClientBuilder.build()).thenReturn(webClient)
//        `when`(webClient.get()).thenReturn(responseSpec)
//        `when`(responseSpec.uri("/schedules/on-calls?")).thenReturn(responseSpec)
//        `when`(responseSpec.retrieve()).thenReturn(responseSpec)
//        `when`(responseSpec.bodyToMono(String::class.java)).thenReturn(Mono.empty())
//
//        // Setting up OpsGenieService
//        val opsGenieService = OpsGenieService()
//        ReflectionTestUtils.setField(opsGenieService, "opsgenieApiKey", "dummyApiKey")
//        ReflectionTestUtils.setField(opsGenieService, "opsgenieUrl", "dummyUrl")
//        ReflectionTestUtils.setField(opsGenieService, "webClientBuilder", webClientBuilder)
//
//        // Test getTeams method
//        val teamList = opsGenieService.getTeams()
//
//        assertNull(teamList)
//    }
//}
