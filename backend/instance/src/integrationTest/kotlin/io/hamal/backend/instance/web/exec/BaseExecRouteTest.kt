package io.hamal.backend.instance.web.exec

import io.hamal.backend.instance.web.BaseRouteTest
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseExecRouteTest : BaseRouteTest() {
    fun createAdhocExec(): SubmittedInvokeAdhocReq {
        val createAdhocExecResponse = httpTemplate
            .post("/v1/adhoc")
            .body(
                InvokeAdhocReq(
                    inputs = InvocationInputs(),
                    code = Code("40 + 2")
                )
            )
            .execute()

        assertThat(createAdhocExecResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createAdhocExecResponse is SuccessHttpResponse) { "request was not successful" }

        return createAdhocExecResponse.result(SubmittedInvokeAdhocReq::class)
    }


}