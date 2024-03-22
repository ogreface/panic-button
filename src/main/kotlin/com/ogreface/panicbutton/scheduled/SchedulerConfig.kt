package com.ogreface.panicbutton.scheduled

import org.quartz.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.JobDetailFactoryBean
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean

@Configuration
class SchedulerConfig {

    @Value("\${panicbutton.update-interval}")
    private val updateSlackTopicsInterval: Long = 300000

    @Bean
    fun syncJobDetail(): JobDetailFactoryBean {
        val jobDetailFactory = JobDetailFactoryBean()
        jobDetailFactory.setJobClass(UpdateSlackTopicJob::class.java)
        jobDetailFactory.setDurability(true)
        return jobDetailFactory
    }

    @Bean
    fun syncJobTrigger(syncJobDetail: JobDetail): SimpleTriggerFactoryBean {
        val triggerFactory = SimpleTriggerFactoryBean()
        triggerFactory.setJobDetail(syncJobDetail)
        triggerFactory.setRepeatInterval(updateSlackTopicsInterval)
        triggerFactory.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY)
        return triggerFactory
    }
}
