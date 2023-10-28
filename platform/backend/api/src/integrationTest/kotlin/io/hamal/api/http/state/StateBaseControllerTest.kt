package io.hamal.api.http.state

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiCorrelatedState
import io.hamal.lib.sdk.api.ApiFuncCreateReq
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import io.hamal.lib.sdk.bridge.BridgeExecCompleteReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

@Suppress("UNCHECKED_CAST")
internal sealed class StateBaseControllerTest : BaseControllerTest() {

    fun createFunc(name: FuncName): ApiSubmittedReqImpl<FuncId> {
        val response = httpTemplate.post("/v1/namespaces/1/funcs")
            .body(
                ApiFuncCreateReq(
                    name = name,
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(ApiSubmittedReqImpl::class) as ApiSubmittedReqImpl<FuncId>
    }

    fun completeExec(execId: ExecId, state: State): ApiSubmittedReqImpl<ExecId> {
        val response = httpTemplate.post("/b1/execs/{execId}/complete")
            .path("execId", execId)
            .body(
                BridgeExecCompleteReq(
                    state = state,
                    result = ExecResult(),
                    events = listOf()
                )
            )
            .execute()
        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(ApiSubmittedReqImpl::class) as ApiSubmittedReqImpl<ExecId>
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

    fun setState(correlatedState: CorrelatedState): ApiSubmittedReqImpl<ExecId> {
        val response = httpTemplate.post("/v1/funcs/{funcId}/states/{correlationId}")
            .path("funcId", correlatedState.correlation.funcId)
            .path("correlationId", correlatedState.correlation.correlationId.value)
            .body(correlatedState.value)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        return response.result(ApiSubmittedReqImpl::class) as ApiSubmittedReqImpl<ExecId>
    }
}