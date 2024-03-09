package io.hamal.api.http.controller.blueprint

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.vo.BlueprintInputs
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.api.ApiBlueprint
import io.hamal.lib.sdk.api.ApiBlueprintCreateRequest
import io.hamal.lib.sdk.api.ApiError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class BlueprintGetControllerTest : BlueprintBaseControllerTest() {

    @Test
    fun `Get blueprint`() {
        val bpId = awaitCompleted(
            createBlueprint(
                ApiBlueprintCreateRequest(
                    name = BlueprintName("TestBlueprint"),
                    inputs = BlueprintInputs(HotObject.builder().set("hamal", "rocks").build()),
                    value = CodeValue("1 + 1")
                )
            )
        ).id

        val getResponse = httpTemplate.get("/v1/blueprints/{bpId}")
            .path("bpId", bpId)
            .execute()


        assertThat(getResponse.statusCode, equalTo(Ok))
        require(getResponse is HttpSuccessResponse) { "request was not successful" }

        with(getResponse.result(ApiBlueprint::class)) {
            assertThat(id, equalTo(bpId))
            assertThat(name, equalTo(BlueprintName("TestBlueprint")))
            assertThat(inputs, equalTo(BlueprintInputs(HotObject.builder().set("hamal", "rocks").build())))
            assertThat(value, equalTo(CodeValue("1 + 1")))
        }
    }

    @Test
    fun `Tries to get blueprint that does not exist`() {
        val getResponse = httpTemplate.get("/v1/blueprints/33333333").execute()
        assertThat(getResponse.statusCode, equalTo(NotFound))
        require(getResponse is HttpErrorResponse) { "request was successful" }

        val error = getResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Blueprint not found"))
    }
}