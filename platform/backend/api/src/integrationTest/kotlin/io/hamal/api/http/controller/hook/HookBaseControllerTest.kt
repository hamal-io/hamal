package io.hamal.api.http.controller.hook

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiHook
import io.hamal.lib.sdk.api.ApiHookCreateRequest
import io.hamal.lib.sdk.api.ApiHookCreateRequested
import io.hamal.lib.sdk.api.ApiHookList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class HookBaseControllerTest : BaseControllerTest() {

    fun createHook(
        req: ApiHookCreateRequest,
        flowId: FlowId = FlowId(1),
    ): ApiHookCreateRequested {
        val response = httpTemplate.post("/v1/flows/{flowId}/hooks")
            .path("flowId", flowId)
            .body(req)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }
        return response.result(ApiHookCreateRequested::class)
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