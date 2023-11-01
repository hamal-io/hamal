package io.hamal.api.http.controller.exec

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiAdhocInvokeReq
import io.hamal.lib.sdk.api.ApiExecInvokeSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class ExecBaseControllerTest : BaseControllerTest() {

    fun createAdhocExec(): ApiExecInvokeSubmitted {
        val createAdhocExecResponse = httpTemplate
            .post("/v1/namespaces/{namespaceId}/adhoc")
            .path("namespaceId", testNamespace.id)
            .body(
                ApiAdhocInvokeReq(
                    inputs = InvocationInputs(),
                    code = CodeValue("40 + 2")
                )
            )
            .execute()

        assertThat(createAdhocExecResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createAdhocExecResponse is HttpSuccessResponse) { "request was not successful" }

        return createAdhocExecResponse.result(ApiExecInvokeSubmitted::class)
    }
}