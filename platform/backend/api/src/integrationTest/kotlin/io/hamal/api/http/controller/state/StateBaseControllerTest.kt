package io.hamal.api.http.controller.state

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiCorrelatedState
import io.hamal.lib.sdk.api.ApiFuncCreateRequest
import io.hamal.lib.sdk.api.ApiFuncCreateRequested
import io.hamal.lib.sdk.api.ApiStateSetRequested
import io.hamal.lib.sdk.bridge.BridgeExecCompleteRequest
import io.hamal.lib.sdk.bridge.BridgeExecCompleteRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class StateBaseControllerTest : BaseControllerTest() {

    fun createFunc(name: FuncName): ApiFuncCreateRequested {
        val response = httpTemplate.post("/v1/namespaces/539/funcs")
            .body(
                ApiFuncCreateRequest(
                    name = name,
                    inputs = FuncInputs(),
                    code = ValueCode(""),
                    codeType = CodeType.Lua54
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }
        return response.result(ApiFuncCreateRequested::class)
    }

    fun completeExec(execId: ExecId, state: ExecState): BridgeExecCompleteRequested {
        val response = httpTemplate.post("/b1/execs/{execId}/complete")
            .path("execId", execId)
            .body(
                BridgeExecCompleteRequest(
                    state = state,
                    result = ExecResult(),
                    events = listOf()
                )
            )
            .execute()
        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }
        return response.result(BridgeExecCompleteRequested::class)
    }

    fun getState(correlation: Correlation) = getState(correlation.funcId, correlation.id)

    fun getState(funcId: FuncId, correlationId: CorrelationId): ApiCorrelatedState {
        val response = httpTemplate.get("/v1/funcs/{funcId}/states/{correlationId}")
            .path("funcId", funcId)
            .path("correlationId", correlationId.stringValue)
            .execute()
        assertThat(response.statusCode, equalTo(Ok))
        require(response is HttpSuccessResponse) { "request was not successful" }

        return response.result(ApiCorrelatedState::class)
    }

    fun setState(correlatedState: CorrelatedState): ApiStateSetRequested {
        val response = httpTemplate.put("/v1/funcs/{funcId}/states/{correlationId}")
            .path("funcId", correlatedState.correlation.funcId)
            .path("correlationId", correlatedState.correlation.id.stringValue)
            .body(correlatedState.value)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }

        return response.result(ApiStateSetRequested::class)
    }
}