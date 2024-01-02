package io.hamal.core.req.handler.blueprint

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.BlueprintCmdRepository
import io.hamal.lib.domain.submitted.BlueprintUpdateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class BlueprintUpdateHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Updates blueprint`() {
        blueprintCmdRepository.create(
            BlueprintCmdRepository.CreateCmd(
                id = CmdId(1),
                blueprintId = BlueprintId(123),
                groupId = testGroup.id,
                name = BlueprintName("TestBlueprint"),
                inputs = BlueprintInputs(
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

        testInstance(submittedUpdateBlueprintReq)

        with(blueprintQueryRepository.get(BlueprintId(123))) {
            assertThat(id, equalTo(BlueprintId(123)))
            assertThat(name, equalTo(BlueprintName("UpdatedBlueprint")))
            assertThat(value, equalTo(CodeValue("40 + 2")))
            assertThat(
                inputs, equalTo(
                    BlueprintInputs(
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

    private val submittedUpdateBlueprintReq by lazy {
        BlueprintUpdateSubmitted(
            id = ReqId(2),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            blueprintId = BlueprintId(123),
            name = BlueprintName("UpdatedBlueprint"),
            inputs = BlueprintInputs(
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
    private lateinit var testInstance: BlueprintUpdateHandler
}