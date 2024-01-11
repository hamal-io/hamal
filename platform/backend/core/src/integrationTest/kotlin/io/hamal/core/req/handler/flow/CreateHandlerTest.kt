package io.hamal.core.request.handler.flow

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.FlowCreateRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.FlowQueryRepository.FlowQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class FlowCreateHandlerTest : BaseReqHandlerTest() {

    @BeforeEach
    fun beforeEach() {
        flowCmdRepository.clear()
    }

    @Test
    fun `Creates flow`() {
        testInstance(submitCreateFlowReq)

        verifySingleFlowExists()
    }

    @Test
    fun `Flow with id already exists`() {
        testInstance(submitCreateFlowReq)

        testInstance(
            FlowCreateRequested(
                id = RequestId(1),
                status = Submitted,
                flowId = FlowId(12345),
                groupId = testGroup.id,
                name = FlowName("another-flow"),
                inputs = FlowInputs(),
                flowType = FlowType.default
            )
        )

        verifySingleFlowExists()
    }

    @Test
    fun `Creates flow with type`() {
        testInstance(
            FlowCreateRequested(
                id = RequestId(1),
                status = Submitted,
                flowId = FlowId(12345),
                groupId = testGroup.id,
                name = FlowName("awesome-flow"),
                inputs = FlowInputs(HotObject.builder().set("hamal", "rocks").build()),
                flowType = FlowType("VerySpecialFlowType")
            )
        )

        with(flowQueryRepository.get(FlowId(12345))) {
            assertThat(id, equalTo(FlowId(12345)))
            assertThat(name, equalTo(FlowName("awesome-flow")))
            assertThat(inputs, equalTo(FlowInputs(HotObject.builder().set("hamal", "rocks").build())))
            assertThat(type, equalTo(FlowType("VerySpecialFlowType")))
        }

        assertThat(flowQueryRepository.list(FlowQuery(groupIds = listOf())), hasSize(1))

    }


    private fun verifySingleFlowExists() {
        flowQueryRepository.list(FlowQuery(groupIds = listOf())).also { flows ->
            assertThat(flows, hasSize(1))
            with(flows.first()) {
                assertThat(id, equalTo(FlowId(12345)))
                assertThat(name, equalTo(FlowName("awesome-flow")))
                assertThat(inputs, equalTo(FlowInputs(HotObject.builder().set("hamal", "rocks").build())))
                assertThat(type, equalTo(FlowType.default))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: FlowCreateHandler

    private val submitCreateFlowReq by lazy {
        FlowCreateRequested(
            id = RequestId(1),
            status = Submitted,
            flowId = FlowId(12345),
            groupId = testGroup.id,
            name = FlowName("awesome-flow"),
            inputs = FlowInputs(
                HotObject.builder().set("hamal", "rocks").build()
            ),
            flowType = FlowType.default
        )
    }
}