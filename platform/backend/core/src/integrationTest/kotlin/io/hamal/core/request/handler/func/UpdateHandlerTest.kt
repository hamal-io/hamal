package io.hamal.core.request.handler.func

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.core.request.handler.NextCommandId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.FuncUpdateRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.FuncCode
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
            assertThat(inputs, equalTo(FuncInputs(HotObject.builder().set("hamal", "rocks").build())))
            assertThat(
                code, equalTo(
                    FuncCode(
                        id = CodeId(123),
                        version = CodeVersion(2)
                    )
                )
            )

            assertThat(deployment.id, equalTo(CodeId(123)))
            assertThat(deployment.version, equalTo(CodeVersion(1)))

            assertThat(codeQueryRepository.get(CodeId(123)).value, equalTo(CodeValue("some code")))
        }
    }

    private val submittedFuncUpdateReq by lazy {
        FuncUpdateRequested(
            requestId = RequestId(500),
            requestedBy = AuthId(2),
            requestStatus = RequestStatus.Submitted,
            workspaceId = testWorkspace.id,
            id = FuncId(1),
            name = FuncName("Func-update"),
            inputs = FuncInputs(HotObject.builder().set("hamal", "rocks").build()),
            code = CodeValue("some code")
        )
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
                codeVersion = CodeVersion(1)
            )
        )
    }

    @Autowired
    private lateinit var testInstance: FuncUpdateHandler
}
