package io.hamal.backend.web.exec

import io.hamal.backend.web.BaseRouteTest
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseExecRouteTest : BaseRouteTest() {
    fun createAdhocExec(): ApiSubmittedReqWithId {
        val createAdhocExecResponse = httpTemplate
            .post("/v1/adhoc")
            .body(
                InvokeAdhocReq(
                    inputs = InvocationInputs(),
                    code = CodeType("40 + 2")
                )
            )
            .execute()

        assertThat(createAdhocExecResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createAdhocExecResponse is SuccessHttpResponse) { "request was not successful" }

        return createAdhocExecResponse.result(ApiSubmittedReqWithId::class)
    }
}