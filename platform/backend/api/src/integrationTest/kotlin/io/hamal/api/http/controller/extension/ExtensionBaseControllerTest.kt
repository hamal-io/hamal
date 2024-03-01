package io.hamal.api.http.controller.extension

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class ExtensionBaseControllerTest : BaseControllerTest() {
    fun createExtension(req: ApiExtensionCreateRequest): ApiExtensionCreateRequested {
        val createResponse = httpTemplate.post("/v1/workspaces/{workspaceId}/extensions")
            .path("workspaceId", testWorkspace.id)
            .body(req)
            .execute()

        assertThat(createResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createResponse is HttpSuccessResponse) { "request was not successful" }
        return createResponse.result(ApiExtensionCreateRequested::class)
    }

    fun getExtension(extensionId: ExtensionId): ApiExtension {
        val getResponse = httpTemplate.get("/v1/extensions/{extensionId}")
            .path("extensionId", extensionId)
            .execute()

        assertThat(getResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getResponse is HttpSuccessResponse) { "request was not successful" }
        return getResponse.result(ApiExtension::class)
    }

    fun getExtensionList(): ApiExtensionList {
        val listResponse = httpTemplate.get("/v1/extensions")
            .parameter("workspace_ids", testWorkspace.id)
            .execute()

        assertThat(listResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listResponse is HttpSuccessResponse) { "request was not successful" }
        return listResponse.result(ApiExtensionList::class)

    }

    fun updateExtension(extensionId: ExtensionId, req: ApiExtensionUpdateRequest): ApiExtensionUpdateRequested {
        val updateResponse = httpTemplate.patch("/v1/extensions/{extensionId}")
            .path("extensionId", extensionId)
            .body(req)
            .execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(updateResponse is HttpSuccessResponse) { "request was not successful" }
        return updateResponse.result(ApiExtensionUpdateRequested::class)
    }

}