package io.hamal.core.request.handler.blueprint

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.BlueprintUpdateRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.BlueprintCmdRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class BlueprintAccountChangePasswordHandlerTest : BaseRequestHandlerTest() {

    @Test
    fun `Updates blueprint`() {
        blueprintCmdRepository.create(
            BlueprintCmdRepository.CreateCmd(
                id = CmdId(1),
                blueprintId = BlueprintId(123),
                name = BlueprintName("TestBlueprint"),
                inputs = BlueprintInputs(HotObject.builder().set("hamal", "rocks").build()),
                value = CodeValue("1 + 1"),
                creatorId = testAccount.id,
                description = BlueprintDescription("TestDescription")
            )
        )

        testInstance(submittedUpdateBlueprintReq)

        with(blueprintQueryRepository.get(BlueprintId(123))) {
            assertThat(id, equalTo(BlueprintId(123)))
            assertThat(name, equalTo(BlueprintName("UpdatedBlueprint")))
            assertThat(value, equalTo(CodeValue("40 + 2")))
            assertThat(inputs, equalTo(BlueprintInputs(HotObject.builder().set("hamal", "updates").build())))
            assertThat(description, equalTo(BlueprintDescription("TestUpdateDescription")))
        }
    }

    private val submittedUpdateBlueprintReq by lazy {
        BlueprintUpdateRequested(
            requestId = RequestId(2),
            requestedBy = AuthId(3),
            requestStatus = RequestStatus.Submitted,
            id = BlueprintId(123),
            name = BlueprintName("UpdatedBlueprint"),
            inputs = BlueprintInputs(HotObject.builder().set("hamal", "updates").build()),
            value = CodeValue("40 + 2"),
            description = BlueprintDescription("TestUpdateDescription")
        )
    }

    @Autowired
    private lateinit var testInstance: BlueprintUpdateHandler
}