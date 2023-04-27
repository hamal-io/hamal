//package io.hamal.application.adapter
//
//import io.hamal.backend.core.domain_notification.CreateDomainNotificationConsumerPort
//import io.hamal.backend.core.domain_notification.DomainNotificationConsumer
//import io.hamal.backend.core.domain_notification.DomainNotificationHandler
//import io.hamal.backend.core.domain_notification.NotifyDomainPort
//import io.hamal.backend.core.domain_notification.notification.DomainNotification
//import io.hamal.lib.log.appender.ProtobufAppender
//import io.hamal.lib.log.broker.BrokerRepository
//import io.hamal.lib.log.consumer.Consumer
//import io.hamal.lib.log.consumer.ProtobufConsumer
//import io.hamal.lib.log.topic.Topic
//import org.springframework.beans.factory.DisposableBean
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
//import java.time.Duration
//import java.util.concurrent.ScheduledFuture
//import kotlin.reflect.KClass
//
//
//class DomainNotificationAdapter(
//    val brokerRepository: BrokerRepository
//) : NotifyDomainPort {
//
//    private val appender = ProtobufAppender(DomainNotification::class, brokerRepository)
//
//    override fun <NOTIFICATION : DomainNotification> invoke(
//        notification: NOTIFICATION,
//    ) {
//        val topic = brokerRepository.resolveTopic(Topic.Name(notification.topic))
//        appender.append(topic, notification)
//    }
//}
//
//
//class DomainNotificationConsumerAdapter(
//    val scheduledExecutorService: ThreadPoolTaskScheduler,
//    val brokerRepository: BrokerRepository
//) : CreateDomainNotificationConsumerPort {
//
//    private val handlerContainer = DomainNotificationHandler.Container.DefaultImpl()
//
//    override fun <NOTIFICATION : DomainNotification> register(
//        clazz: KClass<NOTIFICATION>,
//        handler: DomainNotificationHandler<NOTIFICATION>
//    ): CreateDomainNotificationConsumerPort {
//        handlerContainer.register(clazz, handler)
//        return this
//    }
//
//    override fun create(): DomainNotificationConsumer {
//        return object : DomainNotificationConsumer, DisposableBean {
//
//            private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()
//
//            init {
//                val allDomainTopics = handlerContainer.topics()
//                    .map { Topic.Name(it) }
//                    .map(brokerRepository::resolveTopic)
//
//                allDomainTopics.forEach { topic ->
//                    val consumer = ProtobufConsumer<DomainNotification>(
//                        Consumer.GroupId("01"),
//                        topic,
//                        brokerRepository,
//                        DomainNotification::class
//                    )
//
//                    scheduledTasks.add(
//                        scheduledExecutorService.scheduleAtFixedRate(
//                            {
//                                consumer.consume(100) { notification ->
//                                    handlerContainer[notification::class].forEach { listener ->
//                                        try {
//                                            listener.on(notification)
//                                        } catch (t: Throwable) {
//                                            throw Error(t)
//                                        }
//                                    }
//                                }
//                            }, Duration.ofMillis(10)
//                        )
//                    )
//                }
//            }
//
//            override fun cancel() {
//                scheduledTasks.forEach {
//                    it.cancel(false)
//                }
//            }
//
//            override fun destroy() {
//                cancel()
//            }
//        }
//    }
//
//}