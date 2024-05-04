package io.hamal.core.service

import io.hamal.core.adapter.trigger.TriggerInvokePort
import io.hamal.core.component.WorkerPool
import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.domain.BatchSize.Companion.BatchSize
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.TriggerTypes.Event
import io.hamal.lib.domain.request.TriggerInvokeRequest
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerType.Companion.TriggerType
import io.hamal.repository.api.Auth
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogConsumerId
import io.hamal.repository.api.log.TopicEventConsumerBatchImpl
import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds

@Service
internal class EventTriggerService(
    private val workerPool: WorkerPool,
    internal val generateDomainId: GenerateDomainId,
    internal val triggerInvoke: TriggerInvokePort,
    internal val triggerQueryRepository: TriggerQueryRepository,
    internal val topicRepository: TopicRepository,
    internal val logBrokerRepository: LogBrokerRepository
) : ApplicationListener<ApplicationReadyEvent>, DisposableBean {


    override fun onApplicationEvent(event: ApplicationReadyEvent) {

        triggerQueryRepository.list(
            TriggerQuery(
                types = listOf(TriggerType(Event)),
                limit = Limit(1000),
                workspaceIds = listOf()
            )
        ).forEach { trigger ->
            require(trigger is Trigger.Event)

            val topic = topicRepository.get(trigger.topicId)
            val consumer = TopicEventConsumerBatchImpl(
                consumerId = LogConsumerId(trigger.id.value),
                topicId = topic.logTopicId,
                repository = logBrokerRepository
            )

            triggerConsumers[trigger.id] = consumer

            scheduledTasks.add(
                workerPool.atFixedDelay(100.milliseconds) {
                    if (!shutdown.get()) {
                        try {
                            SecurityContext.with(Auth.System) {
                                consumer.consumeBatch(BatchSize(1)) { events ->
                                    triggerInvoke(
                                        trigger.id,
                                        object : TriggerInvokeRequest {
                                            override val correlationId = trigger.correlationId ?: CorrelationId.default
                                            override val inputs = InvocationInputs()
                                        }
                                    )
                                }
                            }
                        } catch (t: Throwable) {
                            t.printStackTrace()
                        }
                    }
                },
            )
        }

    }

    override fun destroy() {
        shutdown.set(true)
        scheduledTasks.forEach {
            it.cancel(false)
        }
    }

    private val shutdown = AtomicBoolean(false)
    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()
    private val triggerConsumers = mutableMapOf<TriggerId, TopicEventConsumerBatchImpl>()
}