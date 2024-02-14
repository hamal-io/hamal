package io.hamal.core.request.handler.endpoint

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.EndpointMethod.Post
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import io.hamal.repository.api.FuncCmdRepository.CreateCmd
import io.hamal.lib.domain.request.EndpointCreateRequested
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
                workspaceId = testWorkspace.id,
                namespaceId = testNamespace.id,
                name = FuncName("func"),
                inputs = FuncInputs(),
                codeId = CodeId(1),
                codeVersion = CodeVersion(1)
            )
        )

        testInstance(submitCreateEndpointReq)

        endpointRepository.list(EndpointQuery(workspaceIds = listOf())).also { endpoints ->
            assertThat(endpoints, hasSize(1))
            with(endpoints.first()) {
                assertThat(id, equalTo(EndpointId(12345)))
                assertThat(name, equalTo(EndpointName("awesome-endpoint")))
                assertThat(workspaceId, equalTo(testWorkspace.id))
                assertThat(funcId, equalTo(FuncId(23456)))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: EndpointCreateHandler

    private val submitCreateEndpointReq by lazy {
        EndpointCreateRequested(
            id = RequestId(1),
            status = Submitted,
            endpointId = EndpointId(12345),
            workspaceId = testWorkspace.id,
            funcId = FuncId(23456),
            name = EndpointName("awesome-endpoint"),
            method = Post
        )
    }
}