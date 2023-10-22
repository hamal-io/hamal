package io.hamal.core.req.handler.snippet

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.SnippetId
import io.hamal.lib.domain.vo.SnippetInputs
import io.hamal.lib.domain.vo.SnippetName
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.SnippetCmdRepository
import io.hamal.repository.api.submitted_req.SubmittedUpdateSnippetReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class UpdateSnippetHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Updates snippet`() {
        snippetCmdRepository.create(
            SnippetCmdRepository.CreateCmd(
                id = CmdId(1),
                snippetId = SnippetId(123),
                groupId = testGroup.id,
                name = SnippetName("TestSnippet"),
                inputs = SnippetInputs(
                    MapType(
                        mutableMapOf(
                            "hamal" to StringType("rockz")
                        )
                    )
                ),
                value = CodeValue("1 + 1"),
                creatorId = testAccount.id
            )
        )

        testInstance(submittedUpdateSnippetReq)

        with(snippetQueryRepository.get(SnippetId(123))) {
            assertThat(id, equalTo(SnippetId(123)))
            assertThat(name, equalTo(SnippetName("UpdatedSnippet")))
            assertThat(value, equalTo(CodeValue("40 + 2")))
            assertThat(
                inputs, equalTo(
                    SnippetInputs(
                        MapType(
                            mutableMapOf(
                                "hamal" to StringType("updates")
                            )
                        )
                    )
                )
            )
        }
    }

    private val submittedUpdateSnippetReq by lazy {
        SubmittedUpdateSnippetReq(
            reqId = ReqId(2),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            id = SnippetId(123),
            name = SnippetName("UpdatedSnippet"),
            inputs = SnippetInputs(
                MapType(
                    mutableMapOf(
                        "hamal" to StringType("updates")
                    )
                )
            ),
            value = CodeValue("40 + 2")
        )
    }

    @Autowired
    private lateinit var testInstance: UpdateSnippetHandler
}