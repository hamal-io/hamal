package io.hamal.backend.web.state

import io.hamal.backend.web.BaseRouteTest
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.CompleteExecReq
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.hub.domain.ApiCorrelatedState
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReq
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseStateRouteTest : BaseRouteTest() {
    fun createFunc(name: FuncName): ApiSubmittedReqWithId {
        val response = httpTemplate.post("/v1/funcs")
            .body(
                CreateFuncReq(
                    namespaceId = null,
                    name = name,
                    inputs = FuncInputs(),
                    code = CodeType("")
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(ApiSubmittedReqWithId::class)
    }

    fun completeExec(execId: ExecId, state: State): ApiSubmittedReqWithId {
        val response = httpTemplate.post("/v1/execs/{execId}/complete")
            .path("execId", execId)
            .body(
                CompleteExecReq(
                    state = state,
                    events = listOf()
                )
            )
            .execute()
        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(ApiSubmittedReqWithId::class)
    }

    fun getState(correlation: Correlation) = getState(correlation.funcId, correlation.correlationId)

    fun getState(funcId: FuncId, correlationId: CorrelationId): ApiCorrelatedState {
        val response = httpTemplate.get("/v1/funcs/{funcId}/states/{correlationId}")
            .path("funcId", funcId)
            .path("correlationId", correlationId.value)
            .execute()
        assertThat(response.statusCode, equalTo(Ok))
        require(response is SuccessHttpResponse) { "request was not successful" }

        return response.result(ApiCorrelatedState::class)
    }

    fun setState(correlatedState: CorrelatedState): ApiSubmittedReq {
        val response = httpTemplate.post("/v1/funcs/{funcId}/states/{correlationId}")
            .path("funcId", correlatedState.correlation.funcId)
            .path("correlationId", correlatedState.correlation.correlationId.value)
            .body(correlatedState.value)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        return response.result(ApiSubmittedReq::class)
    }
}