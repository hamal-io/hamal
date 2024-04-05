package io.hamal.core.request.handler.endpoint

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.core.request.handler.NextCommandId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.EndpointUpdateRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.EndpointCmdRepository
import io.hamal.repository.api.FuncCmdRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class EndpointAccountChangePasswordHandlerTest : BaseRequestHandlerTest() {

    @Test
    fun `Updates endpoint`() {
        setup()

        testInstance(
            EndpointUpdateRequested(
                requestId = RequestId(500),
                requestedBy = AuthId(2),
                requestStatus = RequestStatus.Submitted,
                id = EndpointId(31),
                name = EndpointName("Updated-Endpoint-Name"),
                workspaceId = testWorkspace.id,
                funcId = FuncId(1)
            )
        )

        with(endpointRepository.get(EndpointId(31))) {
            assertThat(name, equalTo(EndpointName("Updated-Endpoint-Name")))
        }
    }

    private fun setup() {
        codeCmdRepository.create(
            CodeCmdRepository.CreateCmd(
                id = NextCommandId(),
                codeId = CodeId(23),
                workspaceId = testWorkspace.id,
                value = CodeValue("1 + 1")
            )
        )

        funcCmdRepository.create(
            FuncCmdRepository.CreateCmd(
                id = NextCommandId(),
                funcId = FuncId(1),
                workspaceId = testWorkspace.id,
                namespaceId = testNamespace.id,
                name = FuncName("Func-base"),
                inputs = FuncInputs(),
                codeId = CodeId(23),
                codeVersion = CodeVersion(1)
            )
        )

        endpointRepository.create(
            EndpointCmdRepository.CreateCmd(
                id = NextCommandId(),
                endpointId = EndpointId(31),
                name = EndpointName("Created-Endpoint"),
                workspaceId = testWorkspace.id,
                namespaceId = testNamespace.id,
                funcId = FuncId(1),
            )
        )
    }

    @Autowired
    private lateinit var testInstance: EndpointUpdateHandler

}