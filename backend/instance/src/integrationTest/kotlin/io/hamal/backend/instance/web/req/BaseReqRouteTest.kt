package io.hamal.backend.instance.web.req

import io.hamal.backend.instance.web.BaseRouteTest
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.script.api.value.CodeValue
import io.hamal.lib.sdk.domain.ListSubmittedReqsResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseReqRouteTest : BaseRouteTest() {

    fun list(): ListSubmittedReqsResponse {
        val listResponse = httpTemplate.get("/v1/reqs").execute()
        assertThat(listResponse.statusCode, equalTo(HttpStatusCode.Ok))

        require(listResponse is SuccessHttpResponse) { "request was not successful" }
        return listResponse.result(ListSubmittedReqsResponse::class)
    }

    fun adhoc(code: CodeValue = CodeValue("")): SubmittedInvokeAdhocReq {
        return httpTemplate.post("/v1/adhoc").body(
            InvokeAdhocReq(
                inputs = InvocationInputs(),
                code = code
            )
        ).execute(SubmittedInvokeAdhocReq::class)
    }

}