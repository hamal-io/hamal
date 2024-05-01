package io.hamal.core.request.handler.extension

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExtensionCreateRequested
import io.hamal.lib.domain.request.ExtensionUpdateRequested
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName.Companion.ExtensionName
import io.hamal.lib.domain.vo.RequestId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class ExtensionUpdateHandlerTest : BaseRequestHandlerTest() {

    @Test
    fun `Updates extension`() {
        createHandler(submitCreateExtensionReq)
        updateHandler(submittedUpdateExtensionReq)

        assertThat(extensionQueryRepository.get(ExtensionId(1234)).name, equalTo(ExtensionName("UpdateExtension")))
        assertThat(codeQueryRepository.get(CodeId(1)).value, equalTo(ValueCode("40 + 2")))
    }

    private val submitCreateExtensionReq by lazy {
        ExtensionCreateRequested(
            requestId = RequestId(10),
            requestedBy = AuthId(20),
            requestStatus = RequestStatus.Submitted,
            workspaceId = testWorkspace.id,
            id = ExtensionId(1234),
            name = ExtensionName("TestExtension"),
            codeId = CodeId(1),
            code = ValueCode("x='hamal'")
        )
    }

    private val submittedUpdateExtensionReq by lazy {
        ExtensionUpdateRequested(
            requestId = RequestId(1),
            requestedBy = AuthId(2),
            requestStatus = RequestStatus.Submitted,
            workspaceId = testWorkspace.id,
            id = ExtensionId(1234),
            name = ExtensionName("UpdateExtension"),
            code = ValueCode("40 + 2")
        )
    }

    @Autowired
    private lateinit var createHandler: ExtensionCreateHandler

    @Autowired
    private lateinit var updateHandler: ExtensionUpdateHandler
}
