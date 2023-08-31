package io.hamal.backend.instance.service

import io.hamal.backend.instance.component.Async
import io.hamal.backend.instance.req.InvokeExec
import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventPayload
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.GroupId
import io.hamal.repository.api.log.ProtobufBatchConsumer
import io.hamal.repository.api.log.TopicEntry
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration.Companion.milliseconds

@Service
class EventTriggerService(
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
                triggerQueryRepository.list {
                    afterId = TriggerId(SnowflakeId(Long.MAX_VALUE))
                    types = setOf(TriggerType.Event)
                    limit = Limit(10)
                }.forEach { trigger ->
                    require(trigger is EventTrigger)

                    val topic = eventBrokerRepository.getTopic(trigger.topicId)
                    val consumer = ProtobufBatchConsumer(
                        groupId = GroupId(trigger.id.value.value.toString(16)),
                        topic = topic,
                        repository = eventBrokerRepository,
                        valueClass = TopicEntry::class
                    )

                    triggerConsumers[trigger.id] = consumer
                    try {
                        consumer.consumeBatch(1) { entries ->
                            submitRequest(
                                InvokeExec(
                                    execId = generateDomainId(::ExecId),
                                    funcId = trigger.funcId,
                                    correlationId = trigger.correlationId ?: CorrelationId("__default__"),
                                    inputs = InvocationInputs(),
                                    code = funcQueryRepository.get(trigger.funcId).code,
                                    events = entries.map {
                                        Event(
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