package io.hamal.backend.service

import io.hamal.backend.component.Async
import io.hamal.backend.component.EventEmitter
import io.hamal.backend.event.Event
import io.hamal.backend.repository.api.domain.EventTrigger
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.sqlite.log.ProtobufBatchConsumer
import io.hamal.backend.req.InvokeEvent
import io.hamal.backend.req.Request
import io.hamal.backend.service.query.FuncQueryService
import io.hamal.backend.service.query.TriggerQueryService
import io.hamal.lib.common.Shard
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.port.GenerateDomainId
import io.hamal.lib.script.api.value.TableValue
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration.Companion.milliseconds

@Service
class EventTriggerService
@Autowired constructor(
    internal val eventEmitter: EventEmitter,
    internal val funcQueryService: FuncQueryService,
    internal val logBrokerRepository: LogBrokerRepository,
    internal val triggerQueryService: TriggerQueryService,
    internal val request: Request,
    internal val generateDomainId: GenerateDomainId,
    internal val async: Async
) {


    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

//    val consumer: BatchConsumer<Event>
//
//    init {
//        val topic = logBrokerRepository.resolveTopic(TopicName("event-test"))
//        println(topic)
//
//        consumer = ProtobufBatchConsumer(
//            groupId = GroupId("c2"),
//            topic = topic,
//            logBrokerRepository = logBrokerRepository,
//            valueClass = Event::class
//        )
//    }
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

    val triggerConsumers = mutableMapOf<TriggerId, ProtobufBatchConsumer<Event>>()


    @PostConstruct
    fun setup() {
        triggerQueryService.list(
            afterId = TriggerId(0),
            types = setOf(TriggerType.Event),
            limit = 10
        ).forEach { trigger ->
            println("start $trigger")
            require(trigger is EventTrigger)

            val topic = logBrokerRepository.find(trigger.topicId)!!
            val consumer = ProtobufBatchConsumer(
                groupId = GroupId(trigger.id.value.value.toString(16)),
                topic = topic,
                logBrokerRepository = logBrokerRepository,
                valueClass = Event::class
            )

            triggerConsumers[trigger.id] = consumer

            scheduledTasks.add(
                async.atFixedRate(1000.milliseconds) {
                    println(trigger.name)

                    try {
                        consumer.consumeBatch(1) { evts ->
                            request(
                                InvokeEvent(
                                    execId = generateDomainId(Shard(1), ::ExecId),
                                    funcId = trigger.funcId,
                                    correlationId = CorrelationId("__TBD__"),
                                    inputs = InvocationInputs(TableValue()),
                                    secrets = InvocationSecrets(listOf()),
                                )
                            )
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }

//                    consumer.consume(100) { chunkId, evt ->
//                        handlerContainer[evt::class].forEach { handler ->
//                            try {
//                                val cmdId = CmdId(HashUtils.md5("${evt.topic}-${chunkId.value.value}"))
//                                handler.handle(cmdId, evt)
//                            } catch (t: Throwable) {
//                                throw Error(t)
//                            }
//                        }
//                    }
                },
            )
        }
    }

}