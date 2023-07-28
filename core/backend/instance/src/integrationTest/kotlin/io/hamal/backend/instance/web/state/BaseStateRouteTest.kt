package io.hamal.backend.instance.web.state

import io.hamal.backend.instance.web.BaseRouteTest
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.value.CodeValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseStateRouteTest : BaseRouteTest() {
    fun createFunc(name: FuncName): SubmittedCreateFuncReq {
        val response = httpTemplate.post("/v1/funcs")
            .body(
                CreateFuncReq(
                    name = name,
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(SubmittedCreateFuncReq::class)
    }

    fun completeExec(execId: ExecId, state: State): SubmittedCompleteExecReq {
        val response = httpTemplate.post("/v1/execs/${execId.value}/complete")
            .body(
                CompleteExecReq(
                    state = state,
                    events = listOf()
                )
            )
            .execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(SubmittedCompleteExecReq::class)
    }

    fun getState(correlation: Correlation) = getState(correlation.funcId, correlation.correlationId)

    fun getState(funcId: FuncId, correlationId: CorrelationId): CorrelatedState {
        val response = httpTemplate.get("/v1/funcs/${funcId.value.value}/states/${correlationId.value}").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse) { "request was not successful" }

        return response.result(CorrelatedState::class)
    }

    fun setState(correlatedState: CorrelatedState): SubmittedSetStateReq {
        val response =
            httpTemplate.post("/v1/funcs/${correlatedState.correlation.funcId.value}/states/${correlatedState.correlation.correlationId.value}")
                .body(correlatedState.value)
                .execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        return response.result(SubmittedSetStateReq::class)
    }
}