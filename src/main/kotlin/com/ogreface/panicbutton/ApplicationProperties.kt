package com.ogreface.panicbutton

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "panicbutton")
data class ApplicationProperties(
    @NestedConfigurationProperty
    var opsgenie: OpsgenieConfig,
    @NestedConfigurationProperty
    var slack: SlackConfig,
    var updateInterval: Long = 300000,
    var teamidToSlackChannelIdMapping: Map<String,String> = emptyMap(),
    var teamidToSlackUserGroupMapping: Map<String,String> = emptyMap()
)

data class OpsgenieConfig(
    var apiKey: String = "",
    var urL: String = ""
)

data class SlackConfig(
    var url: String = "",
    var botToken:String = ""
)
