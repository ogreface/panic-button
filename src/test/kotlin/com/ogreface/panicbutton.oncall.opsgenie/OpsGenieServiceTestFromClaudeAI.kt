package com.ogreface.panicbutton.oncall.opsgenie


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ogreface.panicbutton.ApplicationProperties
import com.ogreface.panicbutton.data.opsgenie.*
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClient

/* Initially straight out of Claude with just the service got pretty close. I needed to import okhttp.
It also assumed Parent was called something different, but given I didn't give it any of those classes that's not bad.
There was some other cleanup on the data being returned.

The bigger issues are that it hasn't picked up on this being Spring (despite that in the prompt), so this test doesn't conform and the DI aspect
doesn't compile. It also chose to use okhttp's mockwebserver, which is fine, but isn't using it quite right.
 */
@SpringBootTest
@ActiveProfiles("test")
class OpsGenieServiceTestFromClaudeAI {

    private val webClientBuilder: WebClient.Builder = WebClient.builder()

    @Autowired
    var opsGenieService: OpsGenieService = OpsGenieService()

    @Autowired
    var appProperties: ApplicationProperties? = null

    companion object {
        lateinit var mockWebServer: MockWebServer

        @BeforeAll
        @JvmStatic
        fun setUp() {
            mockWebServer = MockWebServer()
            mockWebServer.start()


        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            mockWebServer.shutdown()
        }
    }

    @Test
    fun `getTeams should return list of teams`() {
        val mockUrl:HttpUrl = mockWebServer.url("/")
        appProperties?.opsgenie?.urL = mockUrl.toString()
        opsGenieService.webClientBuilder = webClientBuilder
        // Arrange
        /* I added a few more teams here for more representative data. */
        val onCallResponse = OnCallResponse(
            took = 1.0,
            requestId = "foo",
            data = listOf(
                TeamData(
                    _parent = Parent("team1", "Team 1", enabled = true),
                    onCallParticipants = listOf(
                        OnCallParticipant("user1", "User 1", "user")
                    )
                ),
                TeamData(
                    _parent = Parent("team2", "Team 2", enabled = true),
                    onCallParticipants = listOf(
                        OnCallParticipant("user2", "User 2", "user"),
                        OnCallParticipant("user3", "User 3", "user")
                    )
                ),
                TeamData(
                    _parent = Parent("team3", "Team 3", enabled = true),
                    onCallParticipants = listOf(
                        OnCallParticipant("user3", "User 3", "user"),
                        OnCallParticipant("user5", "User 5", "user")
                    )
                ),
                TeamData(
                    _parent = Parent("team4", "Team 4", enabled = true),
                    onCallParticipants = listOf(
                    )
                )
            )
        )
        val expectedResponse = jacksonObjectMapper().writeValueAsString(onCallResponse)
        mockWebServer.enqueue(MockResponse().setBody(expectedResponse))

        val teams = opsGenieService.getTeams()

        // Assert
        assertEquals(onCallResponse.data.size, teams?.size)
        assertEquals("team1", teams?.get(0)?.id)
        assertEquals("Team 1", teams?.get(0)?.name)
        assertEquals(1, teams?.get(0)?.personList?.size)
        assertEquals("user1", teams?.get(0)?.personList?.get(0)?.id)
        assertEquals("User 1", teams?.get(0)?.personList?.get(0)?.name)
        assertEquals("team2", teams?.get(1)?.id)
        assertEquals("Team 2", teams?.get(1)?.name)
        assertEquals(2, teams?.get(1)?.personList?.size)
    }
}
