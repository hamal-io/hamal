package io.hamal.api.http.controller.exec

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiAdhocInvokeRequest
import io.hamal.lib.sdk.api.ApiExecInvokeRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class ExecBaseControllerTest : BaseControllerTest() {

    fun createAdhocExec(): ApiExecInvokeRequested {
        val createAdhocExecResponse = httpTemplate
            .post("/v1/flows/{flowId}/adhoc")
            .path("flowId", testFlow.id)
            .body(
                ApiAdhocInvokeRequest(
                    inputs = InvocationInputs(),
                    code = CodeValue("40 + 2")
                )
            )
            .execute()

        assertThat(createAdhocExecResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createAdhocExecResponse is HttpSuccessResponse) { "request was not successful" }

        return createAdhocExecResponse.result(ApiExecInvokeRequested::class)
    }
}