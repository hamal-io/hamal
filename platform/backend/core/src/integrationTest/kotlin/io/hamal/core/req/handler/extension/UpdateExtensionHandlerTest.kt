package io.hamal.core.req.handler.extension

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.repository.api.submitted_req.ExtensionSubmittedReq
import io.hamal.repository.api.submitted_req.ExtensionSubmittedUpdateReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class UpdateExtensionHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Updates extension`() {
        createHandler(submitCreateExtensionReq)
        updateHandler(submittedUpdateExtensionReq)

        assertThat(extensionQueryRepository.get(ExtensionId(1234)).name, equalTo(ExtensionName("UpdateExtension")))
        assertThat(codeQueryRepository.get(CodeId(1)).value, equalTo(CodeValue("40 + 2")))
    }

    private val submitCreateExtensionReq by lazy {
        ExtensionSubmittedReq(
            reqId = ReqId(10),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            id = ExtensionId(1234),
            name = ExtensionName("TestExtension"),
            codeId = CodeId(1),
            code = CodeValue("x='hamal'")
        )
    }

    private val submittedUpdateExtensionReq by lazy {
        ExtensionSubmittedUpdateReq(
            reqId = ReqId(1),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            id = ExtensionId(1234),
            name = ExtensionName("UpdateExtension"),
            code = CodeValue("40 + 2")
        )
    }

    @Autowired
    private lateinit var createHandler: CreateExtensionHandler

    @Autowired
    private lateinit var updateHandler: UpdateExtensionHandler
}
