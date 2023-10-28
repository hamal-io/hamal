package io.hamal.api.http.req

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiAdhocInvokeReq
import io.hamal.lib.sdk.api.ApiReqList
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

@Suppress("UNCHECKED_CAST")
internal sealed class ReqBaseControllerTest : BaseControllerTest() {

    fun list(): ApiReqList {
        val listResponse = httpTemplate.get("/v1/reqs").execute()
        assertThat(listResponse.statusCode, equalTo(HttpStatusCode.Ok))

        require(listResponse is SuccessHttpResponse) { "request was not successful" }
        return listResponse.result(ApiReqList::class)
    }

    fun adhoc(code: CodeValue = CodeValue("")): ApiSubmittedReqImpl<ExecId> {
        return httpTemplate.post("/v1/namespaces/{namespaceId}/adhoc")
            .path("namespaceId", testNamespace.id)
            .body(
                ApiAdhocInvokeReq(
                    inputs = InvocationInputs(),
                    code = code
                )
            ).execute(ApiSubmittedReqImpl::class) as ApiSubmittedReqImpl<ExecId>
    }

}