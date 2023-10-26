package io.hamal.core.req.handler.extension

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.ExtensionCmdRepository.CreateCmd
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.api.submitted_req.SubmittedUpdateExtensionReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class UpdateExtensionHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Updates extension`() {
        val createReq = extensionCmdRepository.create(
            CreateCmd(
                id = CmdId(12),
                extId = ExtensionId(1234),
                groupId = testGroup.id,
                name = ExtensionName("TestExtension"),
                code = ExtensionCode(
                    id = CodeId(1),
                    version = CodeVersion(1)
                )
            )
        )

        assertThat(extensionQueryRepository.get(ExtensionId(1234)), notNullValue())

        testInstance(submittedUpdateExtensionReq)

        val ext = extensionQueryRepository.get(ExtensionId(1234))

        with(ext) {
            assertThat(id, equalTo(ExtensionId(1234)))
            assertThat(name, equalTo(ExtensionName("UpdateExtension")))
        }

        assertThat(codeQueryRepository.get(ext.code.id).value, equalTo(CodeValue("x='hamal'")))
    }

    private val submittedUpdateExtensionReq by lazy {
        SubmittedUpdateExtensionReq(
            reqId = ReqId(2),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            id = ExtensionId(1234),
            name = ExtensionName("UpdateExtension"),
            code = CodeValue("x='hamal'")
        )
    }

    @Autowired
    private lateinit var testInstance: UpdateExtensionHandler
}
