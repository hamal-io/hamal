//package io.hamal.application.config
//
//import io.hamal.lib.domain_notification.DomainNotification
//import io.hamal.lib.domain_notification.DomainNotificationConsumer
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import java.util.concurrent.LinkedBlockingQueue
//import java.util.concurrent.TimeUnit
//
//val queue = LinkedBlockingQueue<DomainNotification>()
//
//val queueStore = mutableMapOf<String, LinkedBlockingQueue<DomainNotification>>()
//
//class NotificationConsumer : DomainNotificationConsumer {
//    override fun poll(topic: String): DomainNotification? {
//        return queueStore[topic]?.poll(1, TimeUnit.MILLISECONDS)
//    }
//}
//
//@Configuration
//open class NotificationConfig {
//
//    @Bean
//    fun notificationConsumer(): DomainNotificationConsumer = NotificationConsumer()
//
//}