package io.hamal.api.http.hook

import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.HookName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.sdk.api.ApiCreateHookReq
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiHook
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class HookGetControllerTest : HookBaseControllerTest() {
    @Test
    fun `Hook does not exists`() {
        val getHookResponse = httpTemplate.get("/v1/hooks/33333333").execute()
        assertThat(getHookResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getHookResponse is ErrorHttpResponse) { "request was successful" }

        val error = getHookResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Hook not found"))
    }

    @Test
    fun `Get hook`() {
        val hookId = awaitCompleted(
            createHook(
                ApiCreateHookReq(
                    name = HookName("hook-one"),
                    namespaceId = null
                )
            )
        ).id(::HookId)

        val getHookResponse = httpTemplate.get("/v1/hooks/{hookId}").path("hookId", hookId).execute()
        assertThat(getHookResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getHookResponse is SuccessHttpResponse) { "request was not successful" }

        with(getHookResponse.result(ApiHook::class)) {
            assertThat(id, equalTo(hookId))
            assertThat(name, equalTo(HookName("hook-one")))
        }
    }
}