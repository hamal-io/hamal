package io.hamal.core.request.handler.hook

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.core.request.handler.NextCommandId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.HookUpdateRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.HookCmdRepository.CreateCmd
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class UpdateHookHandlerTest : BaseRequestHandlerTest() {

    @Test
    fun `Updates hook`() {
        setup()

        testInstance(requestedHookUpdate)

        with(hookRepository.get(HookId(31))) {
            assertThat(name, equalTo(HookName("Updated-Hook-Name")))
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

        hookRepository.create(
            CreateCmd(
                id = NextCommandId(),
                hookId = HookId(31),
                name = HookName("Created-Hook"),
                workspaceId = testWorkspace.id,
                namespaceId = testNamespace.id
            )
        )
    }

    private val requestedHookUpdate by lazy {
        HookUpdateRequested(
            requestId = RequestId(500),
            requestedBy = AuthId(2),
            requestStatus = RequestStatus.Submitted,
            id = HookId(31),
            name = HookName("Updated-Hook-Name"),
            workspaceId = testWorkspace.id
        )
    }

    @Autowired
    private lateinit var testInstance: HookUpdateHandler
}