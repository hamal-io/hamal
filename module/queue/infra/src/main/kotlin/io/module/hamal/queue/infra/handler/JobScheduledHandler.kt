package io.module.hamal.queue.infra.handler

import io.hamal.lib.domain_notification.DomainNotificationHandler
import io.hamal.lib.domain_notification.notification.JobDomainNotification.Scheduled
import org.springframework.stereotype.Component

@Component
open class JobScheduledHandler : DomainNotificationHandler<Scheduled> {

    override fun on(notification: Scheduled) {
        println("QUEUE received ${notification}")
    }

//    override fun topic(): String {
//        TODO("Not yet implemented")
//    }

}