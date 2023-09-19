package io.hamal.core.req.handler.metric

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.core.req.handler.exec.CompleteExecHandler
import io.hamal.core.req.handler.exec.FailExecHandler
import io.hamal.lib.domain.EventPayload
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.event.ExecutionCompletedEvent
import io.hamal.repository.api.event.ExecutionFailedEvent
import io.hamal.repository.api.event.HubEvent
import io.hamal.repository.api.submitted_req.SubmittedCompleteExecReq
import io.hamal.repository.api.submitted_req.SubmittedFailExecReq
import io.hamal.repository.memory.MemoryMetricRepository
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass


class MetricServiceTest {

    private val metricRepository: MetricRepository = MemoryMetricRepository

    @BeforeEach
    fun clear() {
        metricRepository.clear()
    }


    @Nested
    internal inner class TestEmitter(
        @Autowired val completeExecHandler: CompleteExecHandler,
        @Autowired val failExecHandler: FailExecHandler
    ) : BaseReqHandlerTest() {

        private inner class TestEventFactory {
            private var count = 0

            private val makeCompletedEvent: (Int) -> Unit = { id ->
                completeExecHandler(
                    SubmittedCompleteExecReq(
                        ReqId(id),
                        ReqStatus.Submitted,
                        groupId = GroupId(23),
                        ExecId(id),
                        state = State(MapType(mutableMapOf("counter" to NumberType(1)))),
                        events = listOf(
                            EventToSubmit(
                                topicName = TopicName("test-completion"),
                                payload = EventPayload(MapType(mutableMapOf("ich" to StringType("habFertsch"))))
                            )
                        )
                    )
                )
            }

            private val makeFailedEvent: (Int) -> Unit = { id ->
                failExecHandler(
                    SubmittedFailExecReq(
                        reqId = ReqId(id),
                        status = ReqStatus.Submitted,
                        id = ExecId(id),
                        groupId = GroupId(23),
                        cause = ErrorType("You have not tried hard enough")
                    )
                )
            }

            private val events: HashMap<KClass<out HubEvent>, (Int) -> Unit> = hashMapOf(
                ExecutionCompletedEvent::class to makeCompletedEvent,
                ExecutionFailedEvent::class to makeFailedEvent
            )

            fun execute(s: KClass<out HubEvent>) {
                if (events.containsKey(s)) {
                    createExec(ExecId(count), ExecStatus.Started)
                    events[s]?.invoke(count)
                    count++
                } else throw NoSuchElementException()
            }

        }

        private val myEmitter = TestEventFactory()


        @Test
        @Disabled
        fun `count completed`() {
            myEmitter.execute(ExecutionCompletedEvent::class)
            Assertions.assertFalse(metricRepository.getData().getMap().isEmpty())
            Assertions.assertEquals(1, metricRepository.getData().getMap().size)
            Assertions.assertEquals(
                1, metricRepository.getData().getMap().getValue(ExecutionCompletedEvent::class.toString())
            )
        }

        @Test
        @Disabled
        fun `count failed`() {
            myEmitter.execute(ExecutionFailedEvent::class)
            Assertions.assertEquals(1, metricRepository.getData().getMap().size)
            Assertions.assertEquals(
                1,
                metricRepository.getData().getMap().getValue(ExecutionFailedEvent::class.toString())
            )
        }

        @Disabled
        fun multithreadTest() {
            val pool = Executors.newFixedThreadPool(4)

            val tasks = List(100) {
                Runnable {
                    myEmitter.execute(ExecutionCompletedEvent::class)
                    myEmitter.execute(ExecutionFailedEvent::class)
                }
            }

            tasks.forEach { pool.submit(it) }
            pool.shutdown()
            pool.awaitTermination(1, TimeUnit.MINUTES)

            for (i in metricRepository.getData().getMap()) {
                Assertions.assertEquals(100, i.value)
            }
        }
    }

}

