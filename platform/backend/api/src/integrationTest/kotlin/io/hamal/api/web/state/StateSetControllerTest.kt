package io.hamal.api.web.state

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.False
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.True
import io.hamal.lib.sdk.api.ApiDefaultSubmittedReq
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiState
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class StateSetControllerTest : StateBaseControllerTest() {
    @Test
    fun `Sets state for a function first time`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id(::FuncId)

        val response = httpTemplate.post("/v1/funcs/{funcId}/states/__CORRELATION__")
            .path("funcId", funcId)
            .body(State(MapType(mutableMapOf("answer" to NumberType(42)))))
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        response.result(ApiDefaultSubmittedReq::class)

        val correlatedState = getState(funcId, CorrelationId("__CORRELATION__"))
        assertThat(correlatedState["answer"], equalTo(NumberType(42)))
    }

    @Test
    fun `Sets state for a function with multiple correlations`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id(::FuncId)

        val correlationOne = Correlation(funcId = funcId, correlationId = CorrelationId("1"))
        val correlationTwo = Correlation(funcId = funcId, correlationId = CorrelationId("2"))

        setState(CorrelatedState(correlationOne, State(MapType(mutableMapOf("result" to True)))))
        setState(CorrelatedState(correlationTwo, State(MapType(mutableMapOf("result" to False)))))

        with(getState(correlationOne)) {
            assertThat(correlation.correlationId, equalTo(CorrelationId("1")))
            assertThat(state, equalTo(ApiState(MapType(mutableMapOf("result" to True)))))
        }

        with(getState(correlationTwo)) {
            assertThat(correlation.correlationId, equalTo(CorrelationId("2")))
            assertThat(state, equalTo(ApiState(MapType((mutableMapOf("result" to False))))))
        }
    }

    @Test
    fun `Updates a state multiple times`() {

        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id(::FuncId)

        val correlation = Correlation(
            correlationId = CorrelationId("SOME_CORRELATION"),
            funcId = funcId
        )

        repeat(5) { currentCount ->
            awaitCompleted(
                setState(
                    CorrelatedState(
                        correlation = correlation,
                        value = State(MapType(mutableMapOf("count" to NumberType(currentCount))))
                    )
                )
            )

            val correlatedState = getState(correlation)
            assertThat(correlatedState["count"], equalTo(NumberType(currentCount)))
        }
    }

    @Test
    fun `Tries to set state but func does not exists`() {
        val response = httpTemplate.post("/v1/funcs/0/states/__CORRELATION__")
            .body(State(MapType(mutableMapOf("answer" to NumberType(42)))))
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))
    }
}