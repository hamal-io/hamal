package io.hamal.api.http.controller.state

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.KuaFalse
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaTrue
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiState
import io.hamal.lib.sdk.api.ApiStateSetRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class StateSetControllerTest : StateBaseControllerTest() {
    @Test
    fun `Sets state for a function first time`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).funcId

        val response = httpTemplate.put("/v1/funcs/{funcId}/states/__CORRELATION__")
            .path("funcId", funcId)
            .body(State(KuaMap(mutableMapOf("answer" to KuaNumber(42)))))
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }

        awaitCompleted(response.result(ApiStateSetRequested::class))

        val correlatedState = getState(funcId, CorrelationId("__CORRELATION__"))
        assertThat(correlatedState["answer"], equalTo(KuaNumber(42)))
    }

    @Test
    fun `Sets state for a function with multiple correlations`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).funcId

        val correlationOne = Correlation(funcId = funcId, correlationId = CorrelationId("1"))
        val correlationTwo = Correlation(funcId = funcId, correlationId = CorrelationId("2"))

        setState(CorrelatedState(correlationOne, State(KuaMap(mutableMapOf("result" to KuaTrue)))))
        setState(CorrelatedState(correlationTwo, State(KuaMap(mutableMapOf("result" to KuaFalse)))))

        with(getState(correlationOne)) {
            assertThat(correlation.correlationId, equalTo(CorrelationId("1")))
            assertThat(state, equalTo(ApiState(KuaMap(mutableMapOf("result" to KuaTrue)))))
        }

        with(getState(correlationTwo)) {
            assertThat(correlation.correlationId, equalTo(CorrelationId("2")))
            assertThat(state, equalTo(ApiState(KuaMap((mutableMapOf("result" to KuaFalse))))))
        }
    }

    @Test
    fun `Updates a state multiple times`() {

        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).funcId

        val correlation = Correlation(
            correlationId = CorrelationId("SOME_CORRELATION"),
            funcId = funcId
        )

        repeat(5) { currentCount ->
            awaitCompleted(
                setState(
                    CorrelatedState(
                        correlation = correlation,
                        value = State(KuaMap(mutableMapOf("count" to KuaNumber(currentCount))))
                    )
                )
            )

            val correlatedState = getState(correlation)
            assertThat(correlatedState["count"], equalTo(KuaNumber(currentCount)))
        }
    }

    @Test
    fun `Tries to set state but func does not exists`() {
        val response = httpTemplate.put("/v1/funcs/0/states/__CORRELATION__")
            .body(State(KuaMap(mutableMapOf("answer" to KuaNumber(42)))))
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is HttpErrorResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))
    }
}