package io.hamal.backend.instance.service

import io.hamal.backend.instance.component.Async
import io.hamal.backend.instance.req.InvokeExec
import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.repository.api.EventTrigger
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.ProtobufBatchConsumer
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventInvocation
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.port.GenerateDomainId
import io.hamal.lib.kua.value.TableValue
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration.Companion.milliseconds

@Service
class EventTriggerService(
    internal val eventBrokerRepository: LogBrokerRepository,
    internal val triggerQueryRepository: TriggerQueryRepository,
    internal val submitRequest: SubmitRequest,
    internal val generateDomainId: GenerateDomainId,
    internal val async: Async,
    private val funcQueryRepository: FuncQueryRepository
) {

    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

    val triggerConsumers = mutableMapOf<TriggerId, ProtobufBatchConsumer<Event>>()

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
                        valueClass = Event::class
                    )

                    triggerConsumers[trigger.id] = consumer
                    try {
                        consumer.consumeBatch(1) { evts ->
                            submitRequest(
                                InvokeExec(
                                    execId = generateDomainId(::ExecId),
                                    funcId = trigger.funcId,
                                    correlationId = trigger.correlationId ?: CorrelationId("__default__"),
                                    inputs = InvocationInputs(TableValue()),
                                    invocation = EventInvocation(evts),
                                    code = funcQueryRepository.get(trigger.funcId).code
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