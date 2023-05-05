package io.hamal.backend.infra.module.trigger.handler

import io.hamal.backend.core.trigger.ManualTriggerCreatedNotification
import io.hamal.lib.ddd.port.HandleDomainNotificationPort

class TriggerCreatedHandler: HandleDomainNotificationPort<ManualTriggerCreatedNotification> {
    override fun handle(notification: ManualTriggerCreatedNotification) {
        println("==================================")
        println(notification)
        println(notification.id.partition())
        println(notification.id.sequence())
        println(notification.id.elapsed())



//        val definition = await
//            .withPollDelay(10.milliseconds.toJavaDuration())
//            .atMost(3.seconds.toJavaDuration())
//            .untilNotNull {
//                println("try to access job definition")
//                DummyDb.jobDefinitions[notification.trigger.jobDefinitionId]!!
//            }
//
//        println(definition)

    }
}