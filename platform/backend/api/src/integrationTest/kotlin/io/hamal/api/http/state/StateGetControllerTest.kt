package io.hamal.api.http.state

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus.Started
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiCorrelatedState
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiState
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class StateGetControllerTest : StateBaseControllerTest() {

    @Test
    fun `Get state`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).funcId

        val execId = createExec(
            execId = ExecId(123),
            status = Started,
            correlation = Correlation(
                funcId = funcId,
                correlationId = CorrelationId("__1__")
            )
        ).id

        awaitCompleted(completeExec(execId, State(MapType(mutableMapOf("hamal" to StringType("rocks"))))).id)

        val response = httpTemplate.get("/v1/funcs/{funcId}/states/__1__").path("funcId", funcId).execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is HttpSuccessResponse) { "request was not successful" }

        val correlatedState = response.result(ApiCorrelatedState::class)
        assertThat(correlatedState.correlation.func.id, equalTo(funcId))
        assertThat(correlatedState.correlation.func.name, equalTo(FuncName("SomeFunc")))
        assertThat(correlatedState.correlation.correlationId, equalTo(CorrelationId("__1__")))
        assertThat(correlatedState.state, equalTo(ApiState(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
    }

    @Test
    fun `Get state for function which was never set before`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).funcId

        val response = httpTemplate.get("/v1/funcs/{funcId}/states/__1__").path("funcId", funcId).execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is HttpSuccessResponse) { "request was not successful" }

        val correlatedState = response.result(ApiCorrelatedState::class)
        assertThat(correlatedState.correlation.func.id, equalTo(funcId))
        assertThat(correlatedState.correlation.func.name, equalTo(FuncName("SomeFunc")))
        assertThat(correlatedState.correlation.correlationId, equalTo(CorrelationId("__1__")))
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