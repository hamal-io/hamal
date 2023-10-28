package io.hamal.core.req.handler.extension

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.repository.api.submitted_req.ExtensionCreateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CreateExtensionHandlerTest : BaseReqHandlerTest() {
    @Test
    fun `Creates Extension`() {
        testInstance(submitCreateExtensionReq)

        val ext = extensionQueryRepository.get(ExtensionId(1234))
        with(ext) {
            assertThat(id, equalTo(ExtensionId(1234)))
            assertThat(name, equalTo(ExtensionName("TestExtension")))
            assertThat(code.id, equalTo(CodeId(1)))
        }

        assertThat(codeQueryRepository.get(ext.code.id).value, equalTo(CodeValue("x='hamal'")))
    }

    private val submitCreateExtensionReq by lazy {
        ExtensionCreateSubmitted(
            reqId = ReqId(1),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            id = ExtensionId(1234),
            name = ExtensionName("TestExtension"),
            codeId = CodeId(1),
            code = CodeValue("x='hamal'")
        )
    }

    @Autowired
    private lateinit var testInstance: CreateExtensionHandler
}
