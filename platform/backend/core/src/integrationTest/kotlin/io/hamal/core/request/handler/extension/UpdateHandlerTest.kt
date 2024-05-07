package io.hamal.core.request.handler.extension

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.ExtensionCreateRequested
import io.hamal.lib.domain.request.ExtensionUpdateRequested
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.CodeId.Companion.CodeId
import io.hamal.lib.domain.vo.CodeValue.Companion.CodeValue
import io.hamal.lib.domain.vo.ExtensionId.Companion.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName.Companion.ExtensionName
import io.hamal.lib.domain.vo.RequestId.Companion.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
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
        assertThat(codeQueryRepository.get(CodeId(1)).value, equalTo(CodeValue("40 + 2")))
    }

    private val submitCreateExtensionReq by lazy {
        ExtensionCreateRequested(
            requestId = RequestId(10),
            requestedBy = AuthId(20),
            requestStatus = RequestStatus(Submitted),
            workspaceId = testWorkspace.id,
            id = ExtensionId(1234),
            name = ExtensionName("TestExtension"),
            codeId = CodeId(1),
            code = CodeValue("x='hamal'")
        )
    }

    private val submittedUpdateExtensionReq by lazy {
        ExtensionUpdateRequested(
            requestId = RequestId(1),
            requestedBy = AuthId(2),
            requestStatus = RequestStatus(Submitted),
            workspaceId = testWorkspace.id,
            id = ExtensionId(1234),
            name = ExtensionName("UpdateExtension"),
            code = CodeValue("40 + 2")
        )
    }

    @Autowired
    private lateinit var createHandler: ExtensionCreateHandler

    @Autowired
    private lateinit var updateHandler: ExtensionUpdateHandler
}
