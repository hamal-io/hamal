package io.hamal.backend.infra.module.trigger.handler

import io.hamal.backend.application.DummyDb
import io.hamal.backend.core.notification.TriggerDomainNotification
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilNotNull
import org.awaitility.kotlin.withPollDelay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class TriggerCreatedHandler : HandleDomainNotificationPort<TriggerDomainNotification.Created> {
    override fun handle(notification: TriggerDomainNotification.Created) {
        println("==================================")
        println(notification)
        println(notification.trigger.id.partition())
        println(notification.trigger.id.sequence())
        println(notification.trigger.id.elapsed())
        DummyDb.triggers[notification.trigger.id] = notification.trigger


        val definition = await
            .withPollDelay(10.milliseconds.toJavaDuration())
            .atMost(3.seconds.toJavaDuration())
            .untilNotNull {
                println("try to access job definition")
                DummyDb.jobDefinitions[notification.trigger.jobDefinitionId]!!
            }

        println(definition)

    }
}