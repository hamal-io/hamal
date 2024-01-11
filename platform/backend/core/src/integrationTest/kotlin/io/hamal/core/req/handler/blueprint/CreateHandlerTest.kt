package io.hamal.core.request.handler.blueprint

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.BlueprintCreateRequested
import io.hamal.lib.domain.vo.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class BlueprintCreateHandlerTest : BaseReqHandlerTest() {

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
        BlueprintCreateRequested(
            id = RequestId(1),
            status = RequestStatus.Submitted,
            groupId = testGroup.id,
            blueprintId = BlueprintId(123),
            name = BlueprintName("TestBlueprint"),
            inputs = BlueprintInputs(HotObject.builder().set("hamal", "rocks").build()),
            value = CodeValue("1 + 1"),
            creatorId = testAccount.id
        )
    }

    @Autowired
    private lateinit var testInstance: BlueprintCreateHandler
}