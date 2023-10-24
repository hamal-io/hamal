package io.hamal.core.req.handler.extension

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.repository.api.ExtensionCmdRepository.CreateCmd
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.api.submitted_req.SubmittedUpdateExtensionReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class UpdateExtensionHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Updates extension`() {
        extensionCmdRepository.create(
            CreateCmd(
                id = CmdId(12),
                extId = ExtensionId(1234),
                groupId = testGroup.id,
                name = ExtensionName("TestExtension"),
                code = ExtensionCode(
                    id = CodeId(3),
                    version = CodeVersion(3)
                )
            )
        )

        testInstance(submittedUpdateExtensionReq)

        with(extensionQueryRepository.get(ExtensionId(1234))) {
            assertThat(id, equalTo(ExtensionId(1234)))
            assertThat(name, equalTo(ExtensionName("UpdateExtension")))
            assertThat(code.id, equalTo(CodeId(23)))
            assertThat(code.version, equalTo(CodeVersion(42)))
        }
    }

    private val submittedUpdateExtensionReq by lazy {
        SubmittedUpdateExtensionReq(
            reqId = ReqId(26),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            id = ExtensionId(1234),
            name = ExtensionName("UpdateExtension"),
            codeId = CodeId(23),
            codeVersion = CodeVersion(42)
        )
    }

    @Autowired
    private lateinit var testInstance: UpdateExtensionHandler
}
