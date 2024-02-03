package io.hamal.core.service

import io.hamal.core.adapter.FuncInvokePort
import io.hamal.core.component.Async
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.request.FuncInvokeRequest
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.TopicEntry
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.api.log.BatchConsumerImpl
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.ConsumerId
import org.springframework.beans.factory.DisposableBean
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds

@Service
internal class EventTriggerService(
    private val async: Async,
    internal val eventBrokerRepository: BrokerRepository,
    internal val generateDomainId: GenerateId,
    internal val invokeFunc: FuncInvokePort,
    internal val triggerQueryRepository: TriggerQueryRepository,
) : ApplicationListener<ContextRefreshedEvent>, DisposableBean {


    override fun onApplicationEvent(event: ContextRefreshedEvent) {
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

                        val topic = eventBrokerRepository.getTopic(trigger.topicId)
                        val consumer = BatchConsumerImpl(
                            consumerId = ConsumerId(trigger.id.value.value.toString(16)),
                            topic = topic,
                            repository = eventBrokerRepository,
                            valueClass = TopicEntry::class
                        )

                        triggerConsumers[trigger.id] = consumer
                        try {
                            consumer.consumeBatch(1) { entries ->
                                invokeFunc(
                                    trigger.funcId,
                                    object : FuncInvokeRequest {
                                        override val correlationId = trigger.correlationId ?: CorrelationId.default
                                        override val inputs = InvocationInputs()
                                        override val invocation = EventInvocation(entries.map {
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
    private val triggerConsumers = mutableMapOf<TriggerId, BatchConsumerImpl<TopicEntry>>()
}