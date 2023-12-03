package io.hamal.core.req.handler.endpoint

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.EndpointMethod.Post
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import io.hamal.repository.api.FuncCmdRepository.CreateCmd
import io.hamal.repository.api.submitted_req.EndpointCreateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class EndpointCreateHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Creates endpoint`() {
        funcCmdRepository.create(
            CreateCmd(
                id = CmdId(1),
                funcId = FuncId(23456),
                groupId = testGroup.id,
                flowId = testFlow.id,
                name = FuncName("func"),
                inputs = FuncInputs(),
                codeId = CodeId(1),
                codeVersion = CodeVersion(1)
            )
        )

        testInstance(submitCreateEndpointReq)

        endpointRepository.list(EndpointQuery(groupIds = listOf())).also { endpoints ->
            assertThat(endpoints, hasSize(1))
            with(endpoints.first()) {
                assertThat(id, equalTo(EndpointId(12345)))
                assertThat(name, equalTo(EndpointName("awesome-endpoint")))
                assertThat(groupId, equalTo(testGroup.id))
                assertThat(funcId, equalTo(FuncId(23456)))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: EndpointCreateHandler

    private val submitCreateEndpointReq by lazy {
        EndpointCreateSubmitted(
            id = ReqId(1),
            status = Submitted,
            endpointId = EndpointId(12345),
            groupId = testGroup.id,
            funcId = FuncId(23456),
            name = EndpointName("awesome-endpoint"),
            method = Post
        )
    }
}