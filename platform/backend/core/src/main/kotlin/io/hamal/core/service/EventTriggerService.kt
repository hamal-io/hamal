package io.hamal.core.service

import io.hamal.core.adapter.FuncInvokePort
import io.hamal.core.component.Async
import io.hamal.lib.common.domain.BatchSize
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.request.FuncInvokeRequest
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.TopicEvent
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogConsumerBatchImpl
import io.hamal.repository.api.log.LogConsumerId
import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds

@Service
internal class EventTriggerService(
    private val async: Async,
    internal val generateDomainId: GenerateId,
    internal val invokeFunc: FuncInvokePort,
    internal val triggerQueryRepository: TriggerQueryRepository,
    internal val topicRepository: TopicRepository,
    internal val logBrokerRepository: LogBrokerRepository
) : ApplicationListener<ApplicationReadyEvent>, DisposableBean {


    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        scheduledTasks.add(
            async.atFixedRate(1.milliseconds) {
                if (!shutdown.get()) {
                    triggerQueryRepository.list(
                        TriggerQuery(
                            afterId = TriggerId(SnowflakeId(Long.MAX_VALUE)),
                            types = listOf(TriggerType.Event),
                            limit = Limit(10),
                            groupIds = listOf()
                        )
                    ).forEach { trigger ->
                        require(trigger is EventTrigger)

                        val topic = topicRepository.get(trigger.topicId)
                        val consumer = LogConsumerBatchImpl(
                            consumerId = LogConsumerId(trigger.id.value),
                            topicId = topic.logTopicId,
                            repository = logBrokerRepository,
                            valueClass = TopicEvent::class
                        )

                        triggerConsumers[trigger.id] = consumer
                        try {
                            consumer.consumeBatch(BatchSize(1)) { events ->
                                invokeFunc(
                                    trigger.funcId,
                                    object : FuncInvokeRequest {
                                        override val correlationId = trigger.correlationId ?: CorrelationId.default
                                        override val inputs = InvocationInputs()
                                        override val invocation = EventInvocation(events.map {
                                            Event(
                                                topic = EventTopic(
                                                    id = topic.id,
                                                    name = topic.name
                                                ),
                                                id = EventId(it.id.value),
                                                payload = EventPayload(it.payload.value)
                                            )
                                        })
                                    }
                                ) {}
                            }
                        } catch (t: Throwable) {
                            t.printStackTrace()
                        }
                    }
                }
            },
        )
    }

    override fun destroy() {
        shutdown.set(true)
        scheduledTasks.forEach {
            it.cancel(false)
        }
    }

    private val shutdown = AtomicBoolean(false)
    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()
    private val triggerConsumers = mutableMapOf<TriggerId, LogConsumerBatchImpl<TopicEvent>>()
}