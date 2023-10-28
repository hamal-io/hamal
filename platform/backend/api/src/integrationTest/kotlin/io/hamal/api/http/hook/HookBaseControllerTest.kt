package io.hamal.api.http.hook

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiHook
import io.hamal.lib.sdk.api.ApiHookCreateReq
import io.hamal.lib.sdk.api.ApiHookList
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import io.hamal.lib.sdk.toReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class HookBaseControllerTest : BaseControllerTest() {

    fun createHook(
        req: ApiHookCreateReq,
        namespaceId: NamespaceId = NamespaceId(1),
    ): ApiSubmittedReqImpl<HookId> {
        val response = httpTemplate.post("/v1/namespaces/{namespaceId}/hooks")
            .path("namespaceId", namespaceId)
            .body(req)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }
        return response.toReq()
    }

    fun listHooks(): ApiHookList {
        val listHooksResponse = httpTemplate.get("/v1/hooks")
            .parameter("group_ids", testGroup.id)
            .execute()

        assertThat(listHooksResponse.statusCode, equalTo(Ok))
        require(listHooksResponse is HttpSuccessResponse) { "request was not successful" }
        return listHooksResponse.result(ApiHookList::class)
    }

    fun getHook(hookId: HookId): ApiHook {
        val getHookResponse = httpTemplate.get("/v1/hooks/{hookId}")
            .path("hookId", hookId)
            .execute()

        assertThat(getHookResponse.statusCode, equalTo(Ok))
        require(getHookResponse is HttpSuccessResponse) { "request was not successful" }
        return getHookResponse.result(ApiHook::class)
    }
}