package io.hamal.core.request.handler.func

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.core.request.handler.NextCommandId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.FuncDeployRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.FuncCmdRepository
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class FuncDeployHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Deploys version without message`() {
        setup(CodeId(5))

        testInstance(
            FuncDeployRequested(
                id = RequestId(500),
                by = AuthId(2),
                status = Submitted,
                workspaceId = testWorkspace.id,
                FuncId(1),
                version = CodeVersion(10),
                message = null
            )
        )

        with(funcQueryRepository.get(FuncId(1))) {
            assertThat(code.version, equalTo(CodeVersion(20)))
            assertThat(deployment.version, equalTo(CodeVersion(10)))
            assertThat(deployment.message, equalTo(DeployMessage.empty))
        }
    }

    @Test
    fun `Deploys version`() {
        setup(CodeId(5))

        testInstance(
            FuncDeployRequested(
                id = RequestId(500),
                by = AuthId(2),
                status = Submitted,
                workspaceId = testWorkspace.id,
                FuncId(1),
                version = CodeVersion(10),
                message = DeployMessage("This function and hamal rocks")
            )
        )

        with(funcQueryRepository.get(FuncId(1))) {
            assertThat(code.version, equalTo(CodeVersion(20)))
            assertThat(deployment.version, equalTo(CodeVersion(10)))
            assertThat(deployment.message, equalTo(DeployMessage("This function and hamal rocks")))
        }
    }

    @Test
    fun `Deploys latest version`() {
        setup(CodeId(5))

        testInstance(
            FuncDeployRequested(
                id = RequestId(500),
                by = AuthId(2),
                status = Submitted,
                workspaceId = testWorkspace.id,
                funcId = FuncId(1),
                version = null,
                message = null
            )
        )

        with(funcQueryRepository.get(FuncId(1))) {
            assertThat(code.version, equalTo(CodeVersion(20)))

            assertThat(deployment.version, equalTo(CodeVersion(20)))
            assertThat(deployment.message, equalTo(DeployMessage.empty))
        }
    }

    private fun setup(codeId: CodeId) {
        codeCmdRepository.create(
            CodeCmdRepository.CreateCmd(
                id = NextCommandId(),
                codeId = codeId,
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
                codeId = codeId,
                codeVersion = CodeVersion(20)
            )
        )
    }

    @Autowired
    private lateinit var testInstance: FuncDeployHandler
}