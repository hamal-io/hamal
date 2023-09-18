package io.hamal.core.service

import io.hamal.core.component.Async
import io.hamal.core.req.InvokeExecReq
import io.hamal.core.req.SubmitRequest
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.*
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.ConsumerId
import io.hamal.repository.api.log.ProtobufBatchConsumer
import io.hamal.repository.api.log.TopicEntry
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration.Companion.milliseconds

@Service
internal class EventTriggerService(
    internal val eventBrokerRepository: BrokerRepository,
    internal val triggerQueryRepository: TriggerQueryRepository,
    internal val submitRequest: SubmitRequest,
    internal val generateDomainId: GenerateDomainId,
    internal val async: Async,
    private val funcQueryRepository: FuncQueryRepository
) {

    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

    val triggerConsumers = mutableMapOf<TriggerId, ProtobufBatchConsumer<TopicEntry>>()

    @PostConstruct
    fun setup() {
        scheduledTasks.add(
            async.atFixedRate(1.milliseconds) {
                triggerQueryRepository.list(
                    TriggerQueryRepository.TriggerQuery(
                        afterId = TriggerId(SnowflakeId(Long.MAX_VALUE)),
                        types = setOf(TriggerType.Event),
                        limit = Limit(10),
                        groupIds = setOf()
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
                        consumer.consumeBatch(1) { entries ->
                            submitRequest(
                                InvokeExecReq(
                                    execId = generateDomainId(::ExecId),
                                    funcId = trigger.funcId,
                                    correlationId = trigger.correlationId ?: CorrelationId("__default__"),
                                    inputs = InvocationInputs(),
                                    code = funcQueryRepository.get(trigger.funcId).code,
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
}