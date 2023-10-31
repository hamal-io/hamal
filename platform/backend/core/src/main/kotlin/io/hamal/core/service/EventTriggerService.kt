package io.hamal.core.service

import io.hamal.core.component.Async
import io.hamal.core.req.InvokeExecReq
import io.hamal.core.req.SubmitRequest
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.ConsumerId
import io.hamal.repository.api.log.ProtobufBatchConsumer
import io.hamal.repository.api.log.TopicEntry
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration.Companion.milliseconds

@Service
internal class EventTriggerService(
    internal val eventBrokerRepository: BrokerRepository,
    internal val triggerQueryRepository: TriggerQueryRepository,
    internal val submitRequest: SubmitRequest,
    internal val generateDomainId: GenerateDomainId,
    private val async: Async,
    private val funcQueryRepository: FuncQueryRepository
) : DisposableBean {

    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

    val triggerConsumers = mutableMapOf<TriggerId, ProtobufBatchConsumer<TopicEntry>>()

    @PostConstruct
    fun setup() {
        scheduledTasks.add(
            async.atFixedRate(1.milliseconds) {
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
                    val consumer = ProtobufBatchConsumer(
                        consumerId = ConsumerId(trigger.id.value.value.toString(16)),
                        topic = topic,
                        repository = eventBrokerRepository,
                        valueClass = TopicEntry::class
                    )

                    triggerConsumers[trigger.id] = consumer
                    try {
                        val func = funcQueryRepository.get(trigger.funcId)
                        consumer.consumeBatch(1) { entries ->
                            submitRequest(
                                InvokeExecReq(
                                    execId = generateDomainId(::ExecId),
                                    funcId = trigger.funcId,
                                    correlationId = trigger.correlationId ?: CorrelationId.default,
                                    inputs = InvocationInputs(),
                                    code = func.code.toExecCode(),
                                    events = entries.map {
                                        Event(
                                            topic = EventTopic(
                                                id = topic.id,
                                                name = topic.name
                                            ),
                                            id = EventId(it.id.value),
                                            payload = EventPayload(it.payload.value)
                                        )
                                    }
                                )
                            )
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
            },
        )
    }

    override fun destroy() {
        scheduledTasks.forEach {
            it.cancel(false)
        }
    }
}