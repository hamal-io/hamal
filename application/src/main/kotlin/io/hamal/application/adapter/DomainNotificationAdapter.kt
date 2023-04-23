package io.hamal.application.adapter

import io.hamal.lib.domain_notification.CreateDomainNotificationConsumerPort
import io.hamal.lib.domain_notification.DomainNotificationConsumer
import io.hamal.lib.domain_notification.DomainNotificationHandler
import io.hamal.lib.domain_notification.NotifyDomainPort
import io.hamal.lib.domain_notification.notification.DomainNotification
import io.hamal.lib.log.ToRecord
import io.hamal.lib.log.topic.Topic
import io.hamal.lib.meta.KeyedOnce
import io.hamal.lib.meta.exception.InternalServerException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import org.springframework.beans.factory.DisposableBean
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.nio.ByteBuffer
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.Path
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

object InMemoryBroker {

    private val topicMapping = KeyedOnce.default<Topic.Id, Topic>()

    fun publish(topicId: Topic.Id, toRecord: ToRecord) {
        val topic = getTopic(topicId)
        topic.append(toRecord)
    }

    val offsets = mutableMapOf<String, Long>()

    fun read(topicId: Topic.Id, consumerId: String): List<Topic.Record> {
        offsets.putIfAbsent(consumerId, 0L)
        val offset = offsets[consumerId]

        val result = getTopic(topicId).read(Topic.Record.Id(offset!!), 1)


        offsets.put(consumerId, offset + result.size)

        return result
    }

    private fun getTopic(topicId: Topic.Id): Topic = topicMapping.invoke(topicId) {
        Topic.open(
            Topic.Config(
                topicId,
                Path("/tmp/hamal/topics")
            )
        )
    }


}


class DomainNotificationAdapter() : NotifyDomainPort {

//    private val producer = InMemoryProducer<String, DomainNotification>()


    @OptIn(ExperimentalSerializationApi::class)
    override fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION) {
        InMemoryBroker.publish(
            Topic.Id(23),
            ToRecord(
                key = ByteBuffer.wrap("".toByteArray()),
                value = ByteBuffer.wrap(Cbor.encodeToByteArray<DomainNotification>(notification)),
                instant = Instant.now()
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

//                val consumer = InMemoryConsumer<String, DomainNotification>(topicIds)
                scheduledTasks.add(
                    scheduledExecutorService.scheduleAtFixedRate(
                        {

                            InMemoryBroker.read(Topic.Id(23), "abc").forEach { record ->
                                println("RECORD ${record.instant}")
                                val notification = Cbor.decodeFromByteArray<DomainNotification>(record.value.array())
                                handlerContainer[notification::class].forEach { receiver ->
                                    try {
                                        receiver.on(notification)
                                    } catch (t: Throwable) {
                                        throw InternalServerException(t)
                                    }
                                }
                            }

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