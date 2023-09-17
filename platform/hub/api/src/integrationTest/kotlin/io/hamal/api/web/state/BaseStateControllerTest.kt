package io.hamal.api.web.state

import io.hamal.api.web.BaseControllerTest
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.hub.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseStateControllerTest : BaseControllerTest() {

    fun createFunc(name: FuncName): HubSubmittedReqWithId {
        val response = httpTemplate.post("/v1/groups/{groupId}/funcs")
            .path("groupId", testGroup.id)
            .body(
                HubCreateFuncReq(
                    namespaceId = null,
                    name = name,
                    inputs = FuncInputs(),
                    code = CodeType("")
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(HubSubmittedReqWithId::class)
    }

    fun completeExec(execId: ExecId, state: State): HubSubmittedReqWithId {
        val response = httpTemplate.post("/v1/execs/{execId}/complete")
            .path("execId", execId)
            .body(
                HubCompleteExecReq(
                    state = state,
                    events = listOf()
                )
            )
            .execute()
        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(HubSubmittedReqWithId::class)
    }

    fun getState(correlation: Correlation) = getState(correlation.funcId, correlation.correlationId)

    fun getState(funcId: FuncId, correlationId: CorrelationId): HubCorrelatedState {
        val response = httpTemplate.get("/v1/funcs/{funcId}/states/{correlationId}")
            .path("funcId", funcId)
            .path("correlationId", correlationId.value)
            .execute()
        assertThat(response.statusCode, equalTo(Ok))
        require(response is SuccessHttpResponse) { "request was not successful" }

        return response.result(HubCorrelatedState::class)
    }

    fun setState(correlatedState: CorrelatedState): HubSubmittedReq {
        val response = httpTemplate.post("/v1/funcs/{funcId}/states/{correlationId}")
            .path("funcId", correlatedState.correlation.funcId)
            .path("correlationId", correlatedState.correlation.correlationId.value)
            .body(correlatedState.value)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        return response.result(HubDefaultSubmittedReq::class)
    }
}