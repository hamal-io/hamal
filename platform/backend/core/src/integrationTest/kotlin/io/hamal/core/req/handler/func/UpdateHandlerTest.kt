package io.hamal.core.req.handler.func

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.core.req.handler.NextCommandId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.FuncCode
import io.hamal.repository.api.FuncDeployment
import io.hamal.repository.api.submitted_req.FuncUpdateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class FuncUpdateHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Updates func`() {
        setup(CodeId(123))

        testInstance(submittedFuncUpdateReq)

        with(funcQueryRepository.get(FuncId(1))) {
            assertThat(name, equalTo(FuncName("Func-update")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
            assertThat(
                code, equalTo(
                    FuncCode(
                        id = CodeId(123),
                        version = CodeVersion(2)
                    )
                )
            )
            assertThat(
                deployment, equalTo(
                    FuncDeployment(
                        id = CodeId(123),
                        version = CodeVersion(1),
                        message = DeployMessage("Initial version")
                    )
                )
            )
            assertThat(codeQueryRepository.get(CodeId(123)).value, equalTo(CodeValue("some code")))
        }
    }

    private val submittedFuncUpdateReq by lazy {
        FuncUpdateSubmitted(
            id = ReqId(500),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            funcId = FuncId(1),
            name = FuncName("Func-update"),
            inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
            code = CodeValue("some code")
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
                codeVersion = CodeVersion(1)
            )
        )
    }

    @Autowired
    private lateinit var testInstance: FuncUpdateHandler
}
