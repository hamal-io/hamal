package io.hamal.backend.instance.service

import io.hamal.backend.instance.component.Async
import io.hamal.backend.instance.req.InvokeEvent
import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.backend.repository.api.log.ProtobufBatchConsumer
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventTrigger
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.port.GenerateDomainId
import io.hamal.lib.script.api.value.TableValue
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration.Companion.milliseconds

@Service
class EventTriggerService<TOPIC : LogTopic>(
    internal val eventBrokerRepository: LogBrokerRepository<TOPIC>,
    internal val triggerQueryRepository: TriggerQueryRepository,
    internal val submitRequest: SubmitRequest,
    internal val generateDomainId: GenerateDomainId,
    internal val async: Async
) {

    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

    val triggerConsumers = mutableMapOf<TriggerId, ProtobufBatchConsumer<TOPIC, Event>>()


    @PostConstruct
    fun setup() {
        triggerQueryRepository.list {
            afterId = TriggerId(0)
            types = setOf(TriggerType.Event)
            limit = Limit(10)
        }.forEach { trigger ->
            println("start $trigger")
            require(trigger is EventTrigger)

            val topic = eventBrokerRepository.getTopic(trigger.topicId)
            val consumer = ProtobufBatchConsumer(
                groupId = GroupId(trigger.id.value.value.toString(16)),
                topic = topic,
                repository = eventBrokerRepository,
                valueClass = Event::class
            )

            triggerConsumers[trigger.id] = consumer

            scheduledTasks.add(
                async.atFixedRate(1000.milliseconds) {
                    println(trigger.name)

                    try {
                        consumer.consumeBatch(1) { evts ->
                            submitRequest(
                                InvokeEvent(
                                    execId = generateDomainId(::ExecId),
                                    funcId = trigger.funcId,
                                    correlationId = CorrelationId("__TBD__"),
                                    inputs = InvocationInputs(TableValue()),
                                )
                            )
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                },
            )
        }
    }

}