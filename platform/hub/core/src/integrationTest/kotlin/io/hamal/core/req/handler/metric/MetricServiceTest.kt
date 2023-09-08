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
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class MetricServiceTest : BaseReqHandlerTest() {
    val metricRepository: MetricRepository = MemoryMetricRepository

    @BeforeEach
    fun clear() {
        metricRepository.clear()
    }

    @Autowired
    private lateinit var completeExecHandler: CompleteExecHandler


    @Autowired
    private lateinit var failExecHandler: FailExecHandler


    @Test
    fun `count completed`() {
        createExec(ExecId(1234), ExecStatus.Started)
        completeExecHandler(submittedCompleteExecReq)
        Assertions.assertEquals(1, metricRepository.getData().getMap().size)
        Assertions.assertEquals(
            1, metricRepository.getData().getMap().getValue(ExecutionCompletedEvent::class.toString())
        )
    }

    @Test
    fun `count failed`() {
        createExec(ExecId(1234), ExecStatus.Started)
        failExecHandler(submittedFailExecReq)
        Assertions.assertEquals(1, metricRepository.getData().getMap().size)
        Assertions.assertEquals(1, metricRepository.getData().getMap().getValue(ExecutionFailedEvent::class.toString()))
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


}