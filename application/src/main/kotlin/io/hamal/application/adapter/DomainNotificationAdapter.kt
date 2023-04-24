package io.hamal.application.adapter

import io.hamal.lib.domain_notification.CreateDomainNotificationConsumerPort
import io.hamal.lib.domain_notification.DomainNotificationConsumer
import io.hamal.lib.domain_notification.DomainNotificationHandler
import io.hamal.lib.domain_notification.NotifyDomainPort
import io.hamal.lib.domain_notification.notification.DomainNotification
import io.hamal.lib.log.broker.DefaultBroker
import io.hamal.lib.log.consumer.ProtobufConsumer
import io.hamal.lib.log.producer.Producer
import io.hamal.lib.log.producer.ProtobufProducer
import io.hamal.lib.log.topic.Topic
import io.hamal.lib.meta.KeyedOnce
import io.hamal.lib.meta.exception.InternalServerException
import org.springframework.beans.factory.DisposableBean
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.time.Duration
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object TopicResolver {
    private val counter = AtomicInteger(0)
    private val topicMapping = KeyedOnce.default<String, Topic.Id>()

    fun resolve(topic: String): Topic.Id {
        return topicMapping.invoke(topic) {
            Topic.Id(counter.incrementAndGet())
        }
    }
}


val broker = DefaultBroker()

object InMemoryBroker {

//    private val topicMapping = KeyedOnce.default<Topic.Id, Topic>()

    val offsets = mutableMapOf<String, Long>()

    fun read(topicId: Topic.Id, consumerId: String): List<Topic.Record> {
        offsets.putIfAbsent(consumerId, 0L)
        val offset = offsets[consumerId]

        val result = getTopic(topicId).read(Topic.Record.Id(offset!!), 1)


        offsets.put(consumerId, offset + result.size)

        return result
    }

    private fun getTopic(topicId: Topic.Id): Topic = broker.getTopic(topicId)


}


class DomainNotificationAdapter() : NotifyDomainPort {

    override fun <NOTIFICATION : DomainNotification> invoke(
        notification: NOTIFICATION,
    ) {

        val r = Producer.Record(
            "KEY",
            String::class,
            notification,
            DomainNotification::class
        )

        ProtobufProducer<String, DomainNotification>(broker).produce(
            Topic.Id(23),
            Producer.Record(
                "KEY",
                String::class,
                notification,
                DomainNotification::class
            )
        )
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
                val topicIds = handlerContainer.topics()
                    .map(TopicResolver::resolve)
                    .toList()

                // FIXME one consumer per topic
                val consumer = ProtobufConsumer(
                    Topic.Id(23),
                    broker,
                    String::class,
                    DomainNotification::class
                )

                scheduledTasks.add(
                    scheduledExecutorService.scheduleAtFixedRate(
                        {
                            consumer.poll().forEach { record ->
                                println("RECORD ${record}")
                                val notification = record.value
                                handlerContainer[notification::class].forEach { listener ->
                                    try {
                                        listener.on(notification)
                                    } catch (t: Throwable) {
                                        throw InternalServerException(t)
                                    }
                                }
                            }
                        }, Duration.ofMillis(1)
                    )
                )
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