package io.hamal.api.web.req

import io.hamal.api.web.BaseControllerTest
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiInvokeAdhocReq
import io.hamal.lib.sdk.api.ApiReqList
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class ReqBaseControllerTest : BaseControllerTest() {

    fun list(): ApiReqList {
        val listResponse = httpTemplate.get("/v1/reqs").execute()
        assertThat(listResponse.statusCode, equalTo(HttpStatusCode.Ok))

        require(listResponse is SuccessHttpResponse) { "request was not successful" }
        return listResponse.result(ApiReqList::class)
    }

    fun adhoc(code: CodeValue = CodeValue("")): ApiSubmittedReqWithId {
        return httpTemplate.post("/v1/groups/{groupId}/adhoc")
            .path("groupId", testGroup.id)
            .body(
                ApiInvokeAdhocReq(
                    inputs = InvocationInputs(),
                    code = code
                )
            ).execute(ApiSubmittedReqWithId::class)
    }

}