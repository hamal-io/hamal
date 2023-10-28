package io.hamal.api.http.exec

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiAdhocInvokeReq
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

@Suppress("UNCHECKED_CAST")
internal sealed class ExecBaseControllerTest : BaseControllerTest() {

    fun createAdhocExec(): ApiSubmittedReqImpl<ExecId> {
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
        require(createAdhocExecResponse is SuccessHttpResponse) { "request was not successful" }

        return createAdhocExecResponse.result(ApiSubmittedReqImpl::class) as ApiSubmittedReqImpl<ExecId>
    }
}