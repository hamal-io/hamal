package io.hamal.core.req.handler.flow

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.FlowQueryRepository.FlowQuery
import io.hamal.repository.api.submitted_req.FlowCreateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CreateFlowHandlerTest : BaseReqHandlerTest() {

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
            FlowCreateSubmitted(
                id = ReqId(1),
                status = Submitted,
                flowId = FlowId(12345),
                groupId = testGroup.id,
                name = FlowName("another-flow"),
                inputs = FlowInputs(),
                type = FlowType.default
            )
        )

        verifySingleFlowExists()
    }

    @Test
    fun `Creates flow with type`() {
        testInstance(
            FlowCreateSubmitted(
                id = ReqId(1),
                status = Submitted,
                flowId = FlowId(12345),
                groupId = testGroup.id,
                name = FlowName("awesome-flow"),
                inputs = FlowInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
                type = FlowType("VerySpecialFlowType")
            )
        )

        with(flowQueryRepository.get(FlowId(12345))) {
            assertThat(id, equalTo(FlowId(12345)))
            assertThat(name, equalTo(FlowName("awesome-flow")))
            assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
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
                assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
                assertThat(type, equalTo(FlowType.default))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: CreateFlowHandler

    private val submitCreateFlowReq by lazy {
        FlowCreateSubmitted(
            id = ReqId(1),
            status = Submitted,
            flowId = FlowId(12345),
            groupId = testGroup.id,
            name = FlowName("awesome-flow"),
            inputs = FlowInputs(
                MapType(mutableMapOf("hamal" to StringType("rocks")))
            ),
            type = FlowType.default
        )
    }
}