package com.ogreface.panicbutton

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class PanicButtonApplication {
}

fun main(args: Array<String>) {
    runApplication<PanicButtonApplication>(*args)
}

