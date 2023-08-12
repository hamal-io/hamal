package io.hamal.backend.instance.web.state

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
import io.hamal.lib.kua.type.FalseValue
import io.hamal.lib.kua.type.DoubleType
import io.hamal.lib.kua.type.TableType
import io.hamal.lib.kua.type.TrueValue
import io.hamal.lib.sdk.domain.ApiError
import io.hamal.lib.sdk.domain.ApiState
import io.hamal.lib.sdk.domain.ApiSubmittedReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class SetStateRouteTest : BaseStateRouteTest() {
    @Test
    fun `Sets state for a function first time`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id(::FuncId)

        val response = httpTemplate.post("/v1/funcs/${funcId.value.value}/states/__CORRELATION__")
            .body(State(TableType("answer" to DoubleType(42))))
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        response.result(ApiSubmittedReq::class)

        val correlatedState = getState(funcId, CorrelationId("__CORRELATION__"))
        assertThat(correlatedState["answer"], equalTo(DoubleType(42)))
    }

    @Test
    fun `Sets state for a function with multiple correlations`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id(::FuncId)

        val correlationOne = Correlation(funcId = funcId, correlationId = CorrelationId("1"))
        val correlationTwo = Correlation(funcId = funcId, correlationId = CorrelationId("2"))

        setState(CorrelatedState(correlationOne, State(TableType("result" to TrueValue))))
        setState(CorrelatedState(correlationTwo, State(TableType("result" to FalseValue))))

        with(getState(correlationOne)) {
            assertThat(correlation.correlationId, equalTo(CorrelationId("1")))
            assertThat(value, equalTo(ApiState(TableType("result" to TrueValue))))
        }

        with(getState(correlationTwo)) {
            assertThat(correlation.correlationId, equalTo(CorrelationId("2")))
            assertThat(value, equalTo(ApiState(TableType("result" to FalseValue))))
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
                        value = State(
                            TableType(
                                "count" to DoubleType(currentCount)
                            )
                        )
                    )
                )
            )

            val correlatedState = getState(correlation)
            assertThat(correlatedState["count"], equalTo(DoubleType(currentCount)))
        }
    }

    @Test
    fun `Tries to set state but func does not exists`() {
        val response = httpTemplate.post("/v1/funcs/0/states/__CORRELATION__")
            .body(State(TableType("answer" to DoubleType(42))))
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))
    }
}