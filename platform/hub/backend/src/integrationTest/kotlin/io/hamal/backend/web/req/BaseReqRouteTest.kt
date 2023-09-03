package io.hamal.backend.web.req

import io.hamal.backend.web.BaseRouteTest
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.hub.domain.ApiReqList
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseReqRouteTest : BaseRouteTest() {

    fun list(): ApiReqList {
        val listResponse = httpTemplate.get("/v1/reqs").execute()
        assertThat(listResponse.statusCode, equalTo(HttpStatusCode.Ok))

        require(listResponse is SuccessHttpResponse) { "request was not successful" }
        return listResponse.result(ApiReqList::class)
    }

    fun adhoc(code: CodeType = CodeType("")): ApiSubmittedReqWithId {
        return httpTemplate.post("/v1/adhoc").body(
            InvokeAdhocReq(
                inputs = InvocationInputs(),
                code = code
            )
        ).execute(ApiSubmittedReqWithId::class)
    }

}