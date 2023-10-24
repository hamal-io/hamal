package io.hamal.core.req.handler.extension

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.repository.api.submitted_req.SubmittedCreateExtensionReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CreateExtensionHandlerTest : BaseReqHandlerTest() {
    @Test
    fun `Creates Extension`() {
        testInstance(submitCreateExtensionReq)

        with(extensionQueryRepository.get(ExtensionId(1234))) {
            assertThat(id, equalTo(ExtensionId(1234)))
            assertThat(name, equalTo(ExtensionName("TestExtension")))
            assertThat(code.id, equalTo(CodeId(1)))
            assertThat(code.version, equalTo(CodeVersion(123)))
        }
    }

    private val submitCreateExtensionReq by lazy {
        SubmittedCreateExtensionReq(
            reqId = ReqId(1),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            id = ExtensionId(1234),
            name = ExtensionName("TestExtension"),
            codeId = CodeId(1),
            codeVersion = CodeVersion(123)
        )
    }

    @Autowired
    private lateinit var testInstance: CreateExtensionHandler
}
