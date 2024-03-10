package io.hamal.core.request.handler.hook

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.HookCreateRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.HookQueryRepository.HookQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CreateHookHandlerTest : BaseRequestHandlerTest() {

    @Test
    fun `Creates hook`() {
        testInstance(submitCreateHookReq)

        hookRepository.list(HookQuery(workspaceIds = listOf())).also { hooks ->
            assertThat(hooks, hasSize(1))
            with(hooks.first()) {
                assertThat(id, equalTo(HookId(12345)))
                assertThat(name, equalTo(HookName("awesome-hook")))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: HookCreateHandler

    private val submitCreateHookReq by lazy {
        HookCreateRequested(
            requestId = RequestId(1),
            requestedBy = AuthId(2),
            requestStatus = Submitted,
            id = HookId(12345),
            workspaceId = testWorkspace.id,
            namespaceId = NamespaceId(23456),
            name = HookName("awesome-hook")
        )
    }
}