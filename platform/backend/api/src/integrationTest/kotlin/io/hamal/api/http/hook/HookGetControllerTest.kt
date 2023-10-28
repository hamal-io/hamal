package io.hamal.api.http.hook

import io.hamal.lib.domain.vo.HookName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiHook
import io.hamal.lib.sdk.api.ApiHookCreateReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class HookGetControllerTest : HookBaseControllerTest() {
    @Test
    fun `Hook does not exists`() {
        val getHookResponse = httpTemplate.get("/v1/hooks/33333333").execute()
        assertThat(getHookResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getHookResponse is HttpErrorResponse) { "request was successful" }

        val error = getHookResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Hook not found"))
    }

    @Test
    fun `Get hook`() {
        val hookId = awaitCompleted(createHook(ApiHookCreateReq(HookName("hook-one")))).id

        val getHookResponse = httpTemplate.get("/v1/hooks/{hookId}").path("hookId", hookId).execute()
        assertThat(getHookResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getHookResponse is HttpSuccessResponse) { "request was not successful" }

        with(getHookResponse.result(ApiHook::class)) {
            assertThat(id, equalTo(hookId))
            assertThat(name, equalTo(HookName("hook-one")))
        }
    }
}