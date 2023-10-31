package io.hamal.api.http.blueprint

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiBlueprint
import io.hamal.lib.sdk.api.ApiBlueprintCreateSubmitted
import io.hamal.lib.sdk.api.ApiCreateBlueprintReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BlueprintBaseControllerTest : BaseControllerTest() {
    fun createBlueprint(req: ApiCreateBlueprintReq): ApiBlueprintCreateSubmitted {
        val createBlueprintResponse = httpTemplate.post("/v1/groups/{groupId}/blueprints")
            .path("groupId", testGroup.id)
            .body(req)
            .execute()

        assertThat(createBlueprintResponse.statusCode, equalTo(Accepted))
        require(createBlueprintResponse is HttpSuccessResponse) { "request was not successful" }
        return createBlueprintResponse.result(ApiBlueprintCreateSubmitted::class)

    }

    fun getBlueprint(blueprintId: BlueprintId): ApiBlueprint {
        val getBlueprintResponse = httpTemplate.get("/v1/blueprints/{bpId}")
            .path("bpId", blueprintId)
            .execute()

        assertThat(getBlueprintResponse.statusCode, equalTo(Ok))
        require(getBlueprintResponse is HttpSuccessResponse) { "request was not successful" }
        return getBlueprintResponse.result(ApiBlueprint::class)
    }
}