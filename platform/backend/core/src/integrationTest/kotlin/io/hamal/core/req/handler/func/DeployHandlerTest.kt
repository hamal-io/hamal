package io.hamal.core.req.handler.func

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.core.req.handler.NextCommandId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.lib.domain.submitted.FuncDeploySubmitted
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class FuncDeployHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Deploys version without message`() {
        setup(CodeId(5))

        testInstance(
            FuncDeploySubmitted(
                id = ReqId(500),
                status = Submitted,
                groupId = testGroup.id,
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
            FuncDeploySubmitted(
                id = ReqId(500),
                status = Submitted,
                groupId = testGroup.id,
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
            FuncDeploySubmitted(
                id = ReqId(500),
                status = Submitted,
                groupId = testGroup.id,
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
                groupId = testGroup.id,
                value = CodeValue("1 + 1")
            )
        )

        funcCmdRepository.create(
            FuncCmdRepository.CreateCmd(
                id = NextCommandId(),
                funcId = FuncId(1),
                groupId = testGroup.id,
                flowId = testFlow.id,
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