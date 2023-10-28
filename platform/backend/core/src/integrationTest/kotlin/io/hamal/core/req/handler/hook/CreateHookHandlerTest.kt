package io.hamal.core.req.handler.hook

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.HookName
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.HookQueryRepository.HookQuery
import io.hamal.repository.api.submitted_req.HookCreateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CreateHookHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Creates hook`() {
        testInstance(submitCreateHookReq)

        hookRepository.list(HookQuery(groupIds = listOf())).also { hooks ->
            assertThat(hooks, hasSize(1))
            with(hooks.first()) {
                assertThat(id, equalTo(HookId(12345)))
                assertThat(name, equalTo(HookName("awesome-hook")))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: CreateHookHandler

    private val submitCreateHookReq by lazy {
        HookCreateSubmitted(
            reqId = ReqId(1),
            status = Submitted,
            id = HookId(12345),
            groupId = testGroup.id,
            namespaceId = NamespaceId(23456),
            name = HookName("awesome-hook")
        )
    }
}