package io.hamal.api.http.controller.req

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiAdhocInvokeRequest
import io.hamal.lib.sdk.api.ApiExecInvokeRequested
import io.hamal.lib.sdk.api.ApiRequestList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class ReqBaseControllerTest : BaseControllerTest() {

    fun list(): ApiRequestList {
        val listResponse = httpTemplate.get("/v1/requests").execute()
        assertThat(listResponse.statusCode, equalTo(HttpStatusCode.Ok))

        require(listResponse is HttpSuccessResponse) { "request was not successful" }
        return listResponse.result(ApiRequestList::class)
    }

    fun adhoc(code: CodeValue = CodeValue("")): ApiExecInvokeRequested {
        return httpTemplate.post("/v1/namespaces/{namespaceId}/adhoc")
            .path("namespaceId", testNamespace.id)
            .body(
                ApiAdhocInvokeRequest(
                    inputs = InvocationInputs(),
                    code = code
                )
            ).execute(ApiExecInvokeRequested::class)
    }

}