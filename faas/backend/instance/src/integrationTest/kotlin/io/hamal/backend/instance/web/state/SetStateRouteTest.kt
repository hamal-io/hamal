package io.hamal.backend.instance.web.state

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.SubmittedSetStateReq
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.value.FalseValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.TableValue
import io.hamal.lib.kua.value.TrueValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class SetStateRouteTest : BaseStateRouteTest() {
    @Test
    fun `Sets state for a function first time`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).funcId

        val response = httpTemplate.post("/v1/funcs/${funcId.value.value}/states/__CORRELATION__")
            .body(State(TableValue("answer" to NumberValue(42))))
            .execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        val result = response.result(SubmittedSetStateReq::class)
        assertThat(result.state.correlation.funcId, equalTo(funcId))
        assertThat(result.state.correlation.correlationId, equalTo(CorrelationId("__CORRELATION__")))

        val correlatedState = getState(funcId, CorrelationId("__CORRELATION__"))
        assertThat(correlatedState["answer"], equalTo(NumberValue(42)))
    }

    @Test
    fun `Sets state for a function with multiple correlations`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).funcId

        val correlationOne = Correlation(funcId = funcId, correlationId = CorrelationId("1"))
        val correlationTwo = Correlation(funcId = funcId, correlationId = CorrelationId("2"))

        setState(CorrelatedState(correlationOne, State(TableValue("result" to TrueValue))))
        setState(CorrelatedState(correlationTwo, State(TableValue("result" to FalseValue))))

        with(getState(correlationOne)) {
            assertThat(correlation.correlationId, equalTo(CorrelationId("1")))
            assertThat(value, equalTo(State(TableValue("result" to TrueValue))))
        }

        with(getState(correlationTwo)) {
            assertThat(correlation.correlationId, equalTo(CorrelationId("2")))
            assertThat(value, equalTo(State(TableValue("result" to FalseValue))))
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
                        value = State(
                            TableValue(
                                "count" to NumberValue(currentCount)
                            )
                        )
                    )
                )
            )

            val correlatedState = getState(correlation)
            assertThat(correlatedState["count"], equalTo(NumberValue(currentCount)))
        }
    }

    @Test
    fun `Tries to set state but func does not exists`() {
        val response = httpTemplate.post("/v1/funcs/0/states/__CORRELATION__")
            .body(State(TableValue("answer" to NumberValue(42))))
            .execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

        val error = response.error(HamalError::class)
        assertThat(error.message, equalTo("Func not found"))
    }
}