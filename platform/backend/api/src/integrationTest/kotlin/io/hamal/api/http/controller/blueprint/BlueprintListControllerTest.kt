package io.hamal.api.http.controller.blueprint

import io.hamal.lib.domain.vo.BlueprintDescription
import io.hamal.lib.domain.vo.BlueprintInputs
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.sdk.api.ApiBlueprintCreateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class BlueprintListControllerTest : BlueprintBaseControllerTest() {

    @Test
    fun `List Blueprints`() {
        repeat(10) { iteration ->
            awaitCompleted(
                createBlueprint(
                    ApiBlueprintCreateRequest(
                        name = BlueprintName("TestBlueprint $iteration"),
                        value = CodeValue("40 + 2"),
                        inputs = BlueprintInputs(),
                        description = BlueprintDescription("TestBlueprintDescription")

                    )
                )
            )
        }


        with(listBlueprint()) {
            assertThat(blueprints, hasSize(10))
            with(blueprints.last()) {
                assertThat(name, equalTo(BlueprintName("TestBlueprint 0")))
                assertThat(description, equalTo(BlueprintDescription("TestBlueprintDescription")))
            }
        }
    }
}