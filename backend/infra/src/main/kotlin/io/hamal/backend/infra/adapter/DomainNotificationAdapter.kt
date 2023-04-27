package io.hamal.backend.infra.adapter

import io.hamal.backend.core.notification.DomainNotification
import io.hamal.backend.core.port.notification.FlushDomainNotificationPort
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.lib.log.appender.ProtobufAppender
import io.hamal.lib.log.broker.BrokerRepository
import io.hamal.lib.log.consumer.Consumer
import io.hamal.lib.log.consumer.ProtobufConsumer
import io.hamal.lib.log.topic.Topic
import org.springframework.beans.factory.DisposableBean
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.time.Duration
import java.util.concurrent.ScheduledFuture
import kotlin.reflect.KClass


class DomainNotificationAdapter(
    val brokerRepository: BrokerRepository,
) : NotifyDomainPort, FlushDomainNotificationPort {

    private val local: ThreadLocal<List<Pair<Topic, DomainNotification>>> =
        ThreadLocal<List<Pair<Topic, DomainNotification>>>()

    private val appender = ProtobufAppender(DomainNotification::class, brokerRepository)

    init {
        local.set(listOf())
    }

    override fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION) {
        val topic = brokerRepository.resolveTopic(Topic.Name(notification.topic))
        if (local.get() == null) {
            local.set(listOf(Pair(topic, notification)))
        } else {
            local.set(local.get().plus(Pair(topic, notification)))
        }
    }

    override fun flush() {
        local.get().forEach { (topic, notification) -> appender.append(topic, notification) }
        local.remove()
    }
}


class DomainNotificationConsumerAdapter(
    val scheduledExecutorService: ThreadPoolTaskScheduler,
    val brokerRepository: BrokerRepository
) : CreateDomainNotificationConsumerPort {

    private val handlerContainer = DomainNotificationHandlerContainerAdapter()

    override fun <NOTIFICATION : DomainNotification> register(
        clazz: KClass<NOTIFICATION>,
        handler: HandleDomainNotificationPort<NOTIFICATION>
    ): CreateDomainNotificationConsumerPort {
        handlerContainer.register(clazz, handler)
        return this
    }

    override fun create(): DomainNotificationConsumer {
        return object : DomainNotificationConsumer, DisposableBean {

            private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

            init {
                val allDomainTopics = handlerContainer.topics()
                    .map { Topic.Name(it) }
                    .map(brokerRepository::resolveTopic)

                allDomainTopics.forEach { topic ->
                    val consumer = ProtobufConsumer<DomainNotification>(
                        Consumer.GroupId("01"),
                        topic,
                        brokerRepository,
                        DomainNotification::class
                    )

                    scheduledTasks.add(
                        scheduledExecutorService.scheduleAtFixedRate(
                            {
                                consumer.consume(100) { notification ->
                                    handlerContainer[notification::class].forEach { listener ->
                                        try {
                                            listener.handle(notification)
                                        } catch (t: Throwable) {
                                            throw Error(t)
                                        }
                                    }
                                }
                            }, Duration.ofMillis(10)
                        )
                    )
                }
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