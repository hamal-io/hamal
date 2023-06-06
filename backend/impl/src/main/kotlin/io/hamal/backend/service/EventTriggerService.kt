package io.hamal.backend.service

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.event.Event
import io.hamal.backend.repository.api.log.BatchConsumer
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.sqlite.log.ProtobufBatchConsumer
import io.hamal.backend.req.Request
import io.hamal.backend.service.query.FuncQueryService
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.domain.vo.port.GenerateDomainId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EventTriggerService
@Autowired constructor(
    internal val eventEmitter: EventEmitter,
    internal val funcQueryService: FuncQueryService,
    internal val logBrokerRepository: LogBrokerRepository,
    internal val request: Request,
    internal val generateDomainId: GenerateDomainId
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


//    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.SECONDS)
//    fun run() {
//        val funcs = funcQueryService.list(FuncId(0), 1)
//        if (funcs.isEmpty()) {
//            return
//        }
//
//        consumer.consumeBatch { evts ->
//            CompletableFuture.runAsync {
//                val trigger = EventTrigger(
//                    id = TriggerId(1),
//                    name = TriggerName("event-trigger"),
//                    funcId = funcs.first().id,
//                    topicId = TopicId(10)
//                )
//
//                request(
//                    InvokeEvent(
//                        execId = generateDomainId(Shard(1), ::ExecId),
//                        funcId = funcs.first().id,
//                        correlationId = CorrelationId("__TBD__"),
//                        inputs = InvocationInputs(listOf()),
//                        secrets = InvocationSecrets(listOf()),
//                    )
//                )
//            }
//        }
//    }

}