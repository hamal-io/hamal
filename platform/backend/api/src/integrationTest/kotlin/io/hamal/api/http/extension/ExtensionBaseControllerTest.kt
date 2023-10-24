package io.hamal.api.http.extension

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.sdk.api.ApiCreateExtensionReq
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiExtension
import io.hamal.lib.sdk.api.ApiExtensionList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class ExtensionBaseControllerTest : BaseControllerTest() {
    fun createExtension(req: ApiCreateExtensionReq): ApiSubmittedReqWithId {
        val createResponse = httpTemplate.post("/v1/groups/{groupId}/extensions")
            .path("groupId", testGroup.id)
            .body(req)
            .execute()

        assertThat(createResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createResponse is SuccessHttpResponse) { "request was successful" }
        return createResponse.result(ApiSubmittedReqWithId::class)
    }

    fun getExtension(extId: ExtensionId): ApiExtension {
        val updateResponse = httpTemplate.get("/v1/extensions/{extId}")
            .path("extId", extId)
            .execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(updateResponse is SuccessHttpResponse) { "request was successful" }
        return updateResponse.result(ApiExtension::class)
    }

    fun getExtensionList(): ApiExtensionList {
        val x = httpTemplate.get("/v1/extensions")
            //TODO()    .parameter("group_ids", testGroup.id)
            .execute()

        assertThat(x.statusCode, equalTo(HttpStatusCode.Ok))
        require(x is SuccessHttpResponse) { "request was successful" }
        return x.result(ApiExtensionList::class)

    }

}