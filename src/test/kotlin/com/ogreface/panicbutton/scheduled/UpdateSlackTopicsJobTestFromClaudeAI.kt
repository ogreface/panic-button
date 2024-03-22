package com.ogreface.panicbutton.scheduled

import com.ogreface.panicbutton.data.Team
import com.ogreface.panicbutton.oncall.opsgenie.OpsGenieService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/*
    Decently small generated test case. Small bits of cleanup/syntax, but otherwise it worked.
 */
@SpringBootTest
class UpdateSlackTopicsJobTestFromClaudeAI {

    val opsGenieServiceMock:OpsGenieService? = mock(OpsGenieService::class.java)

    @Autowired
    val syncJob:UpdateSlackTopicJob? = null

    @Test
    fun `executeInternal should call opsGenieService getTeams`() {
        // Arrange
        syncJob?.opsGenieService = opsGenieServiceMock
        val teams = listOf(Team("team1", "Team 1", emptyList()))
        `when`(opsGenieServiceMock?.getTeams()).thenReturn(teams)

        //val syncJob = UpdateSlackTopicJobs()
        val context = mock(JobExecutionContext::class.java)

        // Act
        syncJob?.executeInternal(context)

        // Assert
        verify(opsGenieServiceMock, times(1))?.getTeams()
    }

    @Test
    fun `executeInternal should throw JobExecutionException when opsGenieService throws exception`() {
        // Arrange
        syncJob?.opsGenieService = opsGenieServiceMock
        `when`(opsGenieServiceMock?.getTeams()).thenThrow(RuntimeException("Error"))

        //val syncJob = UpdateSlackTopicJobs()
        val context = mock(JobExecutionContext::class.java)

        // Act & Assert
        assertThrows<JobExecutionException> {
            syncJob?.executeInternal(context)
        }
    }
}
