package io.hamal.core.req.handler.func

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.core.req.handler.NextCommandId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.submitted_req.FuncDeployLatestSubmitted
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal object FuncDeployLatestHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Deploys version`() {
        setup(CodeId(5))

        testInstance(submittedFuncDeployReq)

        with(funcQueryRepository.get(FuncId(1))) {
            assertThat(code.version, equalTo(CodeVersion(20)))
            assertThat(code.deployedVersion, equalTo(CodeVersion(20)))
        }
    }

    private val submittedFuncDeployReq by lazy {
        FuncDeployLatestSubmitted(
            id = ReqId(500),
            status = Submitted,
            groupId = testGroup.id,
            FuncId(1),
        )
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
    private lateinit var testInstance: FuncDeployLatestHandler
}