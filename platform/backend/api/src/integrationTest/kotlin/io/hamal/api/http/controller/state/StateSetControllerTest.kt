package io.hamal.api.http.controller.state

import io.hamal.lib.common.hot.HotNumber
import io.hamal.lib.common.hot.HotObject
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
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiState
import io.hamal.lib.sdk.api.ApiStateSetRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class StateSetControllerTest : StateBaseControllerTest() {
    @Test
    fun `Sets state for a function first time`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id

        val response = httpTemplate.put("/v1/funcs/{funcId}/states/__CORRELATION__")
            .path("funcId", funcId)
            .body(State(HotObject.builder().set("answer", 42).build()))
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }

        awaitCompleted(response.result(ApiStateSetRequested::class))

        val correlatedState = getState(funcId, CorrelationId("__CORRELATION__"))
        assertThat(correlatedState["answer"], equalTo(HotNumber(42)))
    }

    @Test
    fun `Sets state for a function with multiple correlations`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id

        val correlationOne = Correlation(funcId = funcId, id = CorrelationId("1"))
        val correlationTwo = Correlation(funcId = funcId, id = CorrelationId("2"))

        setState(CorrelatedState(correlationOne, State(HotObject.builder().set("result", true).build())))
        setState(CorrelatedState(correlationTwo, State(HotObject.builder().set("result", false).build())))

        with(getState(correlationOne)) {
            assertThat(correlation.id, equalTo(CorrelationId("1")))
            assertThat(state, equalTo(ApiState(HotObject.builder().set("result", true).build())))
        }

        with(getState(correlationTwo)) {
            assertThat(correlation.id, equalTo(CorrelationId("2")))
            assertThat(state, equalTo(ApiState(HotObject.builder().set("result", false).build())))
        }
    }

    @Test
    fun `Updates a state multiple times`() {

        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id

        val correlation = Correlation(
            id = CorrelationId("SOME_CORRELATION"),
            funcId = funcId
        )

        repeat(5) { currentCount ->
            awaitCompleted(
                setState(
                    CorrelatedState(
                        correlation = correlation,
                        value = State(HotObject.builder().set("count", currentCount).build())
                    )
                )
            )

            val correlatedState = getState(correlation)
            assertThat(correlatedState["count"], equalTo(HotNumber(currentCount)))
        }
    }

    @Test
    fun `Tries to set state but func does not exists`() {
        val response = httpTemplate.put("/v1/funcs/0/states/__CORRELATION__")
            .body(State(HotObject.builder().set("answer", 42).build()))
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is HttpErrorResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))
    }
}