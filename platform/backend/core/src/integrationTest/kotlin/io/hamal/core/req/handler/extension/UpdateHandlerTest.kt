package io.hamal.core.req.handler.extension

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.submitted.ExtensionCreateSubmitted
import io.hamal.lib.domain.submitted.ExtensionUpdateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class ExtensionUpdateHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Updates extension`() {
        createHandler(submitCreateExtensionReq)
        updateHandler(submittedUpdateExtensionReq)

        assertThat(extensionQueryRepository.get(ExtensionId(1234)).name, equalTo(ExtensionName("UpdateExtension")))
        assertThat(codeQueryRepository.get(CodeId(1)).value, equalTo(CodeValue("40 + 2")))
    }

    private val submitCreateExtensionReq by lazy {
        ExtensionCreateSubmitted(
            id = ReqId(10),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            extensionId = ExtensionId(1234),
            name = ExtensionName("TestExtension"),
            codeId = CodeId(1),
            code = CodeValue("x='hamal'")
        )
    }

    private val submittedUpdateExtensionReq by lazy {
        ExtensionUpdateSubmitted(
            id = ReqId(1),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            extensionId = ExtensionId(1234),
            name = ExtensionName("UpdateExtension"),
            code = CodeValue("40 + 2")
        )
    }

    @Autowired
    private lateinit var createHandler: ExtensionCreateHandler

    @Autowired
    private lateinit var updateHandler: ExtensionUpdateHandler
}
