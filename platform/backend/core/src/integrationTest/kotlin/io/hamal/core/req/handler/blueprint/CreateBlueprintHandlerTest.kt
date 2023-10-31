package io.hamal.core.req.handler.blueprint

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.submitted_req.BlueprintCreateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CreateBlueprintHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Creates blueprint`() {
        testInstance(submitCreateBlueprintReq)

        with(blueprintQueryRepository.get(BlueprintId(123))) {
            assertThat(id, equalTo(BlueprintId(123)))
            assertThat(name, equalTo(BlueprintName("TestBlueprint")))
            assertThat(value, equalTo(CodeValue("1 + 1")))
            assertThat(creatorId, equalTo(testAccount.id))
        }
    }

    private val submitCreateBlueprintReq by lazy {
        BlueprintCreateSubmitted(
            id = ReqId(1),
            status = ReqStatus.Submitted,
            groupId = testGroup.id,
            blueprintId = BlueprintId(123),
            name = BlueprintName("TestBlueprint"),
            inputs = BlueprintInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
            value = CodeValue("1 + 1"),
            creatorId = testAccount.id
        )
    }

    @Autowired
    private lateinit var testInstance: CreateBlueprintHandler
}