package io.hamal.backend.instance.web.state

import io.hamal.backend.instance.web.BaseRouteTest
import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.sdk.domain.DequeueExecsResponse
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

    fun invokeFunc(funcId: FuncId, correlationId: CorrelationId): SubmittedInvokeOneshotReq {
        val response = httpTemplate.post("/v1/funcs/${funcId.value.value}/exec")
            .body(
                InvokeOneshotReq(
                    correlationId = correlationId,
                    inputs = InvocationInputs()
                )
            ).execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        return response.result(SubmittedInvokeOneshotReq::class)
    }

    fun startExec(): DequeueExecsResponse {
        val dequeueResponse = httpTemplate.post("/v1/dequeue").execute()
        assertThat(dequeueResponse.statusCode, equalTo(HttpStatusCode.Ok))

        require(dequeueResponse is SuccessHttpResponse) { "request was not successful" }
        return dequeueResponse.result(DequeueExecsResponse::class)
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

}