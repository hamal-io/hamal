package io.hamal.backend.infra.module.trigger.handler

import io.hamal.backend.core.notification.TriggerDomainNotification
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort

class TriggerCreatedHandler: HandleDomainNotificationPort<TriggerDomainNotification.Created> {
    override fun handle(notification: TriggerDomainNotification.Created) {
        println("==================================")
        println(notification)
        println(notification.trigger.id.partition())
        println(notification.trigger.id.sequence())
        println(notification.trigger.id.elapsed())



//        val definition = await
//            .withPollDelay(10.milliseconds.toJavaDuration())
//            .atMost(3.seconds.toJavaDuration())
//            .untilNotNull {
//                println("try to access flow definition")
//                DummyDb.flowDefinitions[notification.trigger.flowDefinitionId]!!
//            }
//
//        println(definition)

    }
}