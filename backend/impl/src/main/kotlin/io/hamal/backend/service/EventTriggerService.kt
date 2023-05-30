package io.hamal.backend.service

import io.hamal.backend.event.Event
import io.hamal.backend.event.EventInvocationEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.domain.EventTrigger
import io.hamal.backend.repository.api.log.BatchConsumer
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.sqlite.log.ProtobufBatchConsumer
import io.hamal.backend.service.query.FuncQueryService
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Service
class EventTriggerService
@Autowired constructor(
    internal val eventEmitter: EventEmitter,
    internal val funcQueryService: FuncQueryService,
    internal val logBrokerRepository: LogBrokerRepository
) {

    val consumer: BatchConsumer<Event>

    init {
        val topic = logBrokerRepository.resolveTopic(TopicName("event-test"))
        println(topic)

        consumer = ProtobufBatchConsumer(
            groupId = GroupId("c2"),
            topic = topic,
            logBrokerRepository = logBrokerRepository,
            valueClass = Event::class
        )
    }


    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.SECONDS)
    fun run() {

        val funcs = funcQueryService.list(FuncId(0), 1)
        if (funcs.isEmpty()) {
            return
        }

        consumer.consumeBatch { evts ->
            CompletableFuture.runAsync {
                val trigger = EventTrigger(
                    id = TriggerId(1),
                    name = TriggerName("event-trigger"),
                    funcId = funcs.first().id,
                    topicId = TopicId(10)
                )

                eventEmitter.emit(
                    EventInvocationEvent(
                        reqId = ReqId(123),
                        shard = trigger.shard,
                        func = funcQueryService.get(trigger.funcId),
                        correlationId = CorrelationId("__TBD__"), //FIXME
                        inputs = InvocationInputs(listOf()),
                        secrets = InvocationSecrets(listOf()),
                        trigger = trigger,
                        events = evts
                    )
                )
            }
        }
    }

}