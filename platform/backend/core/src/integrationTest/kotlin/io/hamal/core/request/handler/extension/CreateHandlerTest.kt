package io.hamal.core.request.handler.extension

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExtensionCreateRequested
import io.hamal.lib.domain.vo.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class ExtensionCreateHandlerTest : BaseRequestHandlerTest() {
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
        ExtensionCreateRequested(
            requestId = RequestId(1),
            requestedBy = AuthId(2),
            requestStatus = RequestStatus.Submitted,
            workspaceId = testWorkspace.id,
            id = ExtensionId(1234),
            name = ExtensionName("TestExtension"),
            codeId = CodeId(1),
            code = CodeValue("x='hamal'")
        )
    }

    @Autowired
    private lateinit var testInstance: ExtensionCreateHandler
}
