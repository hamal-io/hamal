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
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.event.ExecutionCompletedEvent
import io.hamal.repository.api.event.ExecutionFailedEvent
import io.hamal.repository.api.submitted_req.SubmittedCompleteExecReq
import io.hamal.repository.api.submitted_req.SubmittedFailExecReq
import io.hamal.repository.memory.MemoryMetricRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


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

        private fun emitCompleted() {
            createExec(ExecId(1234), ExecStatus.Started)
            completeExecHandler(submittedCompleteExecReq)
        }

        private fun emitFailed() {
            createExec(ExecId(1234), ExecStatus.Started)
            failExecHandler(submittedFailExecReq)
        }

        private val submittedCompleteExecReq = SubmittedCompleteExecReq(
            reqId = ReqId(10),
            status = ReqStatus.Submitted,
            id = ExecId(1234),
            state = State(MapType(mutableMapOf("counter" to NumberType(1)))),
            events = listOf(
                EventToSubmit(
                    topicName = TopicName("test-completion"),
                    payload = EventPayload(MapType(mutableMapOf("ich" to StringType("habFertsch"))))
                )
            ),
        )

        private val submittedFailExecReq = SubmittedFailExecReq(
            reqId = ReqId(10),
            status = ReqStatus.Submitted,
            id = ExecId(1234),
            cause = ErrorType("You have not tried hard enough")
        )

        @Test
        fun `count completed`() {
            emitCompleted()
            Assertions.assertFalse(metricRepository.getData().getMap().isEmpty())
            Assertions.assertEquals(1, metricRepository.getData().getMap().size)
            Assertions.assertEquals(
                1, metricRepository.getData().getMap().getValue(ExecutionCompletedEvent::class.toString())
            )
        }

        @Test
        fun `count failed`() {
            emitFailed()
            Assertions.assertEquals(1, metricRepository.getData().getMap().size)
            Assertions.assertEquals(
                1,
                metricRepository.getData().getMap().getValue(ExecutionFailedEvent::class.toString())
            )
        }

        /*
        @Test
        fun multithreadTest() {
            val pool = Executors.newFixedThreadPool(4)

              val tasks = List(100) {
                  Runnable {
                      TODO()
                  }
              }

              tasks.forEach { pool.submit(it) }
              pool.shutdown()
              pool.awaitTermination(1, TimeUnit.MINUTES)

              for (i in metricRepository.getData().getMap()) {
                  // Assertions.assertEquals(100, i.value)
              }
          }
        }*/
    }
}

