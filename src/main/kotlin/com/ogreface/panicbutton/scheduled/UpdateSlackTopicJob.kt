package com.ogreface.panicbutton.scheduled

import com.ogreface.panicbutton.ApplicationProperties
import com.ogreface.panicbutton.SlackService
import com.ogreface.panicbutton.oncall.opsgenie.OpsGenieService
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class UpdateSlackTopicJob : QuartzJobBean() {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    var opsGenieService: OpsGenieService? = null

    @Autowired
    var slackService: SlackService? = null

    @Autowired
    lateinit var appProperties: ApplicationProperties


    @Throws(JobExecutionException::class)
    public override fun executeInternal(context: JobExecutionContext) {
        logger.info("Starting sync job")

        try {
            val teams = opsGenieService?.getTeams()
            logger.info("Sync job completed successfully. Fetched ${teams?.size} teams")


            val teamsToProcess = teams?.filter {
                it.name in appProperties.teamidToSlackChannelIdMapping.keys
            }
            logger.info("Matched ${teamsToProcess?.size} teams from channel configuration")
            teamsToProcess?.forEach { it ->
                val channelId = appProperties.teamidToSlackChannelIdMapping[it.name]
                val onCallNames:String = it.personList.joinToString(", ") { it.name }
                val message = "$it.name: $onCallNames is on call."
                logger.info("Updating topic for channel $channelId: $message.")
                slackService?.updateChannelTopic(it)
            }

            val teamsToProcessUserGroups = teams?.filter {
                it.name in appProperties.teamidToSlackUserGroupMapping.keys
            }
            logger.info("Matched ${teamsToProcessUserGroups?.size} teams from user group configuration")
            teamsToProcessUserGroups?.forEach { it ->
                val userGroupId = appProperties.teamidToSlackUserGroupMapping[it.name]
                val onCallNames:String = it.personList.joinToString(", ") { it.name }
                val message = "$it.name: $onCallNames is on call."
                logger.info("Updating usergroup for  $userGroupId: $message.")
                slackService?.updateTeamUserGroup(it)
            }


        } catch (ex: Exception) {
            logger.error("Error occurred during sync job execution: ${ex.message}", ex)
            throw JobExecutionException(ex)
        }
    }
}
