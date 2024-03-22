package com.ogreface.panicbutton

import com.ogreface.panicbutton.data.Person
import com.ogreface.panicbutton.data.Team
import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.conversations.ConversationsInfoRequest
import com.slack.api.methods.request.conversations.ConversationsSetTopicRequest
import com.slack.api.methods.request.usergroups.users.UsergroupsUsersUpdateRequest
import com.slack.api.methods.request.users.UsersLookupByEmailRequest
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import com.slack.api.methods.response.conversations.ConversationsInfoResponse
import com.slack.api.methods.response.conversations.ConversationsSetTopicResponse
import com.slack.api.methods.response.usergroups.users.UsergroupsUsersUpdateResponse
import com.slack.api.methods.response.users.UsersLookupByEmailResponse
import com.slack.api.model.Topic
import com.slack.api.model.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


/* Out of claude, the any (without T) didn't work right. Have to specify the type so the correct function is called
   A few other smaller syntactical issues too, like it called builder on the response which doesn't have the lombok annotation
   Missing annotations, mocks, and things too.
*/
@SpringBootTest
@ActiveProfiles("test")
class SlackServiceTestFromClaudeAI {

    @Mock
    private lateinit var slack: Slack

    @Mock
    private lateinit var methodsClient: MethodsClient

    @Mock
    private lateinit var appProperties: ApplicationProperties

    @Autowired
    private lateinit var slackService: SlackService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        slackService.slack = slack
        slackService.appProperties = appProperties
        `when`(slack.methods(any())).thenReturn(methodsClient)

    }

    @Test
    fun `test updateChannelTopic with successful response`() {
        val team = Team("team1", "Team 1", listOf(Person("user1", "User 1", "user1@example.com", type = "user")))
        val user = User()
        user.id = "U123"
        `when`(appProperties.teamidToSlackChannelIdMapping).thenReturn(mapOf("Team 1" to "C123"))
        val userEmailResponse = UsersLookupByEmailResponse()
        userEmailResponse.user = user
        userEmailResponse.isOk = true
        val conversationResponse = ConversationsSetTopicResponse()
        conversationResponse.isOk = true

        val conversationInfo = ConversationsInfoResponse()
        conversationInfo.isOk = true
        conversationInfo.channel = com.slack.api.model.Conversation()
        conversationInfo.channel.topic = Topic()

        `when`(methodsClient.usersLookupByEmail(any<UsersLookupByEmailRequest>())).thenReturn(userEmailResponse)
        `when`(methodsClient.conversationsInfo(any<ConversationsInfoRequest>())).thenReturn(conversationInfo)
        `when`(methodsClient.conversationsSetTopic(any<ConversationsSetTopicRequest>())).thenReturn(conversationResponse)

        slackService.updateChannelTopic(team)

        verify(methodsClient, times(1)).usersLookupByEmail(any<UsersLookupByEmailRequest>())
        verify(methodsClient, times(1)).conversationsSetTopic(any<ConversationsSetTopicRequest>())
    }

    @Test
    fun `test updateTeamUserGroup with successful response`() {
        val team = Team("team1", "Team 1", listOf(Person("user1", "User 1", "user1@example.com", type= "user")))
        val user = User()
        user.id = "U123"
        `when`(appProperties.teamidToSlackUserGroupMapping).thenReturn(mapOf("Team 1" to "UG123"))
        val userEmailResponse = UsersLookupByEmailResponse()
        userEmailResponse.user = user
        userEmailResponse.isOk = true
        val userGroupResponse = UsergroupsUsersUpdateResponse()
        userGroupResponse.isOk = true
        `when`(methodsClient.usersLookupByEmail(any<UsersLookupByEmailRequest>())).thenReturn(userEmailResponse)
        `when`(methodsClient.usergroupsUsersUpdate(any<UsergroupsUsersUpdateRequest>())).thenReturn(userGroupResponse)

        slackService.updateTeamUserGroup(team)

        verify(methodsClient, times(1)).usersLookupByEmail(any<UsersLookupByEmailRequest>())
        verify(methodsClient, times(1)).usergroupsUsersUpdate(any<UsergroupsUsersUpdateRequest>())
    }

    @Test
    fun `test lookupSlackUserByEmail with successful response`() {
        val email = "user1@example.com"
        val user = User()
        user.id = "U123"
        val userEmailResponse = UsersLookupByEmailResponse()
        userEmailResponse.user = user
        userEmailResponse.isOk = true
        `when`(methodsClient.usersLookupByEmail(any<UsersLookupByEmailRequest>())).thenReturn(userEmailResponse)

        val result = slackService.lookupSlackUserByEmail(methodsClient, email)

        assertEquals(user, result)
        verify(methodsClient, times(1)).usersLookupByEmail(any<UsersLookupByEmailRequest>())
    }
}
