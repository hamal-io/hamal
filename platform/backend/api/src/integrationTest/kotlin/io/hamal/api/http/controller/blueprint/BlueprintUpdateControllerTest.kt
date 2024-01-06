package io.hamal.api.http.controller.blueprint

import io.hamal.lib.domain.vo.BlueprintInputs
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.api.ApiBlueprintUpdateRequest
import io.hamal.lib.sdk.api.ApiBlueprintUpdateRequested
import io.hamal.lib.sdk.api.ApiBlueprintCreateRequest
import io.hamal.lib.sdk.api.ApiError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class BlueprintUpdateControllerTest : BlueprintBaseControllerTest() {

    @Test
    fun `Updates blueprint`() {
        val bp = awaitCompleted(
            createBlueprint(
                ApiBlueprintCreateRequest(
                    name = BlueprintName("TestBlueprint"),
                    value = CodeValue("40 + 2"),
                    inputs = BlueprintInputs()
                )
            )
        )

        val updateResponse = httpTemplate.patch("/v1/blueprints/{bpId}")
            .path("bpId", bp.blueprintId)
            .body(
                ApiBlueprintUpdateRequest(
                    name = BlueprintName("Other"),
                    value = CodeValue("1 + 1"),
                    inputs = BlueprintInputs(KuaMap(mutableMapOf("hamal" to KuaString("createdInputs"))))
                )
            )
            .execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(updateResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateResponse.result(ApiBlueprintUpdateRequested::class)
        awaitCompleted(submittedReq)
        val bpId = submittedReq.blueprintId

        with(getBlueprint(bpId)) {
            assertThat(id, equalTo(bpId))
            assertThat(name, equalTo(BlueprintName("Other")))
            assertThat(value, equalTo(CodeValue("1 + 1")))
            assertThat(inputs, equalTo(BlueprintInputs(KuaMap(mutableMapOf("hamal" to KuaString("createdInputs"))))))
        }
    }

    @Test
    fun `Updates blueprint without updating values`() {
        val bp = awaitCompleted(
            createBlueprint(
                ApiBlueprintCreateRequest(
                    name = BlueprintName("TestBlueprint"),
                    value = CodeValue("40 + 2"),
                    inputs = BlueprintInputs(KuaMap(mutableMapOf("hamal" to KuaString("createdInputs"))))
                )
            )
        )

        val updateResponse = httpTemplate.patch("/v1/blueprints/{bpId}")
            .path("bpId", bp.blueprintId)
            .body(
                ApiBlueprintUpdateRequest(
                    name = null,
                    value = null,
                    inputs = null
                )
            )
            .execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(updateResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateResponse.result(ApiBlueprintUpdateRequested::class)
        awaitCompleted(submittedReq)

        val bpId = submittedReq.blueprintId

        with(getBlueprint(bpId)) {
            assertThat(id, equalTo(bpId))
            assertThat(name, equalTo(BlueprintName("TestBlueprint")))
            assertThat(value, equalTo(CodeValue("40 + 2")))
            assertThat(inputs, equalTo(BlueprintInputs(KuaMap(mutableMapOf("hamal" to KuaString("createdInputs"))))))
        }
    }


    @Test
    fun `Tries to update blueprint that does not exist`() {
        val updateResponse = httpTemplate.patch("/v1/blueprints/333333")
            .body(
                ApiBlueprintUpdateRequest(
                    name = BlueprintName("TestBlueprint"),
                    value = CodeValue("40 + 2"),
                    inputs = BlueprintInputs()
                )
            )
            .execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(updateResponse is HttpErrorResponse) { "request was successful" }

        val error = updateResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Blueprint not found"))
    }
}