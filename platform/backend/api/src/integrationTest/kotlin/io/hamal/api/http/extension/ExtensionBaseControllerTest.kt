package io.hamal.api.http.extension

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.*
import io.hamal.lib.sdk.toReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class ExtensionBaseControllerTest : BaseControllerTest() {
    fun createExtension(req: ApiExtensionCreateReq): ApiSubmittedReqImpl<ExtensionId> {
        val createResponse = httpTemplate.post("/v1/groups/{groupId}/extensions")
            .path("groupId", testGroup.id)
            .body(req)
            .execute()

        assertThat(createResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createResponse is HttpSuccessResponse) { "request was successful" }
        return createResponse.toReq()
    }

    fun getExtension(extId: ExtensionId): ApiExtension {
        val getResponse = httpTemplate.get("/v1/extensions/{extId}")
            .path("extId", extId)
            .execute()

        assertThat(getResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getResponse is HttpSuccessResponse) { "request was successful" }
        return getResponse.result(ApiExtension::class)
    }

    fun getExtensionList(): ApiExtensionList {
        val listResponse = httpTemplate.get("/v1/extensions")
            .parameter("group_ids", testGroup.id)
            .execute()

        assertThat(listResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listResponse is HttpSuccessResponse) { "request was successful" }
        return listResponse.result(ApiExtensionList::class)

    }

    fun updateExtension(extId: ExtensionId, req: ApiExtensionUpdateReq): ApiSubmittedReqImpl<ExtensionId> {
        val updateResponse = httpTemplate.patch("/v1/extensions/{extId}/update")
            .path("extId", extId)
            .body(req)
            .execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(updateResponse is HttpSuccessResponse) { "request was successful" }
        return updateResponse.toReq()
    }

}