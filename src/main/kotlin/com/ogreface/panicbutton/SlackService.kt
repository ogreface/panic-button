package com.ogreface.panicbutton

import com.ogreface.panicbutton.data.Team
import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.conversations.ConversationsInfoRequest
import com.slack.api.methods.request.conversations.ConversationsSetTopicRequest
import com.slack.api.methods.request.usergroups.users.UsergroupsUsersUpdateRequest
import com.slack.api.methods.request.users.UsersLookupByEmailRequest
import com.slack.api.methods.response.users.UsersLookupByEmailResponse
import com.slack.api.model.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SlackService {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    var appProperties: ApplicationProperties? = null

    var slack: Slack? = null

    fun getSlackClient(): MethodsClient {
        if (slack == null) {
            slack = Slack.getInstance()
        }
        return slack?.methods(appProperties?.slack?.botToken)!!
    }

    fun updateChannelTopic(team: Team) {
        val methodsClient: MethodsClient = getSlackClient()

        val userIds = team.personList.map { it.username }.mapNotNull { lookupSlackUserByEmail(methodsClient, it) }
        when {
            userIds.isEmpty() -> {
                logger.error("No Slack users found for team ${team.name}")
                return
            }

            userIds.size > 1 -> {
                logger.error("Multiple Slack users found for team ${team.name}")
                return
            }
        }
        val onCallPerson = userIds.map { "<@" + it.id + ">" }.joinToString(", ")

        val message = "${team.name}: $onCallPerson is on call."

        /* Check the topic first. If it's the same, we skip since Slack will otherwise post a
        message "Panic Button Set the channel topic" even when it's the same
         */
        val channelId = appProperties?.teamidToSlackChannelIdMapping?.get(team.name)

        var currentTopicRequest = ConversationsInfoRequest.builder()
            .channel(channelId)
            .build()
        val topicResponse = methodsClient.conversationsInfo(currentTopicRequest)

        when {
            topicResponse.channel.topic.value == message -> {
                logger.info("Topic for team ${team.name} is already up to date in Slack channel $channelId")
                return
            }
            else -> {

                val request = ConversationsSetTopicRequest.builder()
                    .channel(channelId)
                    .topic(message)
                    .build()
                val response = methodsClient.conversationsSetTopic(request)

                if (response.isOk) {
                    logger.info("Updated topic for team ${team.name} to Slack channel $channelId")
                } else {
                    logger.error("Failed to update topic for team ${team.name} to Slack channel $channelId. Error: ${response.error}")
                }
            }
        }


    }

    fun updateTeamUserGroup(team: Team) {
        val methodsClient: MethodsClient = getSlackClient()

        val userGroupId = appProperties?.teamidToSlackUserGroupMapping?.get(team.name)
        val userIds = team.personList.map { it.username }.mapNotNull { lookupSlackUserByEmail(methodsClient, it)?.id }
        val request = UsergroupsUsersUpdateRequest.builder()
            .usergroup(userGroupId)
            .users(userIds)
            .build()

        val response = methodsClient.usergroupsUsersUpdate(request)

        if (response.isOk) {
            logger.info("Updated team ${team.name} with usergroup $userGroupId to $userIds")
        } else {
            logger.error("Failed to make usergroup updates for team ${team.name} and usergroup $userGroupId. Error: ${response.error}")
        }
    }

    fun lookupSlackUserByEmail(methodsClient: MethodsClient, email: String): User? {
        val request = UsersLookupByEmailRequest.builder()
            .email(email)
            .build()

        val response = methodsClient.usersLookupByEmail(request)

        return if (response.isOk) {
            response.user
        } else {
            logger.error("Failed to lookup Slack user by email $email. Error: ${response.error}")
            null

        }
    }
}
