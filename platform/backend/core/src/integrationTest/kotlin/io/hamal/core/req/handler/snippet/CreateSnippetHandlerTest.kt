package io.hamal.core.req.handler.snippet

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.submitted_req.SubmittedCreateSnippetReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CreateSnippetHandlerTest : BaseReqHandlerTest() {

    @Autowired
    private lateinit var testInstance: CreateSnippetHandler

    @Test
    fun `Creates snippet`() {
        testInstance(submitCreateSnippetReq)

        val res = snippetQueryRepository.get(SnippetId(123))
        with(res) {
            assertThat(id, equalTo(SnippetId(123)))
            assertThat(name, equalTo(SnippetName("TestSnippet")))
            assertThat(value, equalTo(CodeValue("1 + 1")))
            assertThat(accountId, equalTo(AccountId(123)))
        }
    }

    private val submitCreateSnippetReq by lazy {
        SubmittedCreateSnippetReq(
            reqId = ReqId(1),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            id = SnippetId(123),
            name = SnippetName("TestSnippet"),
            inputs = SnippetInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
            value = CodeValue("1 + 1"),
            accountId = AccountId(123)
        )
    }
}