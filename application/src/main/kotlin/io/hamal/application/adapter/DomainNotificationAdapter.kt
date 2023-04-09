package io.hamal.application.adapter

import io.hamal.application.config.queueStore
import io.hamal.lib.ddd.base.DomainNotification
import io.hamal.lib.ddd.port.NotifyDomainPort
import org.springframework.stereotype.Component
import java.util.concurrent.LinkedBlockingQueue

@Component
class DomainNotificationAdapter<NOTIFICATION : DomainNotification>(
) : NotifyDomainPort<NOTIFICATION> {

    override fun invoke(notification: NOTIFICATION) {
//        println(notification)
        queueStore.putIfAbsent(notification.javaClass.name, LinkedBlockingQueue())
        queueStore[notification.javaClass.name]!!.add(notification)
    }

//    class SpringNotification<NOTIFICATION : DomainNotification>(notification: NOTIFICATION) :
//        ApplicationEvent(notification) {
//
//    }

}