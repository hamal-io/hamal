package io.hamal.api.http.controller.blueprint

import io.hamal.lib.domain.vo.BlueprintInputs
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiBlueprintCreateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class BlueprintCreateControllerTest : BlueprintBaseControllerTest() {

    @Test
    fun `Creates blueprint`() {
        val res = createBlueprint(
            ApiBlueprintCreateRequest(
                name = BlueprintName("TestBlueprint"),
                inputs = BlueprintInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
                value = CodeValue("13 + 37")
            )
        )

        awaitCompleted(res)

        with(blueprintQueryRepository.get(res.blueprintId)) {
            assertThat(inputs, equalTo(BlueprintInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
            assertThat(name, equalTo(BlueprintName("TestBlueprint")))
            assertThat(value, equalTo(CodeValue("13 + 37")))
        }
    }
}