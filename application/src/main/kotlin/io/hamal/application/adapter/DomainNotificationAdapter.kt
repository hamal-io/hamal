package io.hamal.application.adapter

import io.hamal.lib.domain_notification.*
import org.springframework.beans.factory.DisposableBean
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object TopicResolver {
    private val counter = AtomicInteger(0)
//    private val topicMapping = KeyedOnce.default<String, TopicId>()
//
//    fun resolve(topic: String): TopicId {
//        return topicMapping.invoke(topic) {
//            TopicId(counter.incrementAndGet())
//        }
//    }
}

class DomainNotificationAdapter() : NotifyDomainPort {

//    private val producer = InMemoryProducer<String, DomainNotification>()

    override fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION) {
//        producer(
//            listOf(
//                Message(
//                    Message.Meta(
//                        version = 0x01,
//                        instant = Instant.now(),
//                        topicId = TopicResolver.resolve(notification.topic),
//                        partition = Partition(1),
//                        offset = MessageOffset(0),
//                        crc32 = 32
//                    ),
//                    "SomeKey",
//                    notification
//                )
//            )
//        )
        TODO()
    }
}


class DomainNotificationConsumerAdapter(
    val scheduledExecutorService: ThreadPoolTaskScheduler
) : CreateDomainNotificationConsumerPort {

    private val handlerContainer = DomainNotificationHandler.Container.DefaultImpl()

    override fun <NOTIFICATION : DomainNotification> register(
        clazz: KClass<NOTIFICATION>,
        handler: DomainNotificationHandler<NOTIFICATION>
    ): CreateDomainNotificationConsumerPort {
        handlerContainer.register(clazz, handler)
        return this
    }

    override fun create(): DomainNotificationConsumer {
        return object : DomainNotificationConsumer, DisposableBean {
            private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

            init {
//                val topicIds = handlerContainer.topics()
//                    .map(TopicResolver::resolve)
//                    .toList()
//
//                val consumer = InMemoryConsumer<String, DomainNotification>(topicIds)
//                scheduledTasks.add(
//                    scheduledExecutorService.scheduleAtFixedRate(
//                        {
//                            consumer().forEach { message ->
//                                val notification = message.value
//                                handlerContainer[notification::class].forEach { receiver ->
//                                    try {
//                                        receiver.on(notification)
//                                    } catch (t: Throwable) {
//                                        throw InternalServerException(t)
//                                    }
//                                }
//                            }
//                        }, Duration.ofMillis(1)
//                    )
//                )
            }

            override fun cancel() {
                scheduledTasks.forEach {
                    it.cancel(false)
                }
            }

            override fun destroy() {
                cancel()
            }
        }
    }

}