package io.hamal.api.http.controller.state

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.ExecStates.Started
import io.hamal.lib.domain.vo.CorrelationId.Companion.CorrelationId
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecState
import io.hamal.lib.domain.vo.FuncName.Companion.FuncName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.api.ApiCorrelatedState
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiState
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class StateGetControllerTest : StateBaseControllerTest() {

    @Test
    fun `Get state`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id

        val execId = createExec(
            execId = ExecId(123),
            status = Started,
            correlation = Correlation(
                funcId = funcId,
                id = CorrelationId("__1__")
            )
        ).id

        awaitCompleted(completeExec(execId, ExecState(ValueObject.builder().set("hamal", "rocks").build())).id)

        val response = httpTemplate.get("/v1/funcs/{funcId}/states/__1__").path("funcId", funcId).execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is HttpSuccessResponse) { "request was not successful" }

        val correlatedState = response.result(ApiCorrelatedState::class)
        assertThat(correlatedState.correlation.func.id, equalTo(funcId))
        assertThat(correlatedState.correlation.func.name, equalTo(FuncName("SomeFunc")))
        assertThat(correlatedState.correlation.id, equalTo(CorrelationId("__1__")))
        assertThat(correlatedState.state, equalTo(ApiState(ValueObject.builder().set("hamal", "rocks").build())))
    }

    @Test
    fun `Get state for function which was never set before`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id

        val response = httpTemplate.get("/v1/funcs/{funcId}/states/__1__").path("funcId", funcId).execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is HttpSuccessResponse) { "request was not successful" }

        val correlatedState = response.result(ApiCorrelatedState::class)
        assertThat(correlatedState.correlation.func.id, equalTo(funcId))
        assertThat(correlatedState.correlation.func.name, equalTo(FuncName("SomeFunc")))
        assertThat(correlatedState.correlation.id, equalTo(CorrelationId("__1__")))
        assertThat(correlatedState.state, equalTo(ApiState()))
    }

    @Test
    fun `Tries to get state but func does not exists`() {
        val response = httpTemplate.get("/v1/funcs/123456765432/states/1").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.NotFound))
        require(response is HttpErrorResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))
    }
}