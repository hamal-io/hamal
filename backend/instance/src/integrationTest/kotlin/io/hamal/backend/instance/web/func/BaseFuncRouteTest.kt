package io.hamal.backend.instance.web.func

import io.hamal.backend.instance.web.BaseRouteTest
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.req.SubmittedCreateFuncReq
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.domain.ListFuncsResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseFuncRouteTest : BaseRouteTest() {
    fun createFunc(req: CreateFuncReq): SubmittedCreateFuncReq {
        val createTopicResponse = httpTemplate.post("/v1/funcs").body(req).execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createTopicResponse is SuccessHttpResponse) { "request was not successful" }

        return createTopicResponse.result(SubmittedCreateFuncReq::class)
    }

    fun listFuncs(): ListFuncsResponse {
        val listFuncsResponse = httpTemplate.get("/v1/funcs").execute()
        assertThat(listFuncsResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(listFuncsResponse is SuccessHttpResponse) { "request was not successful" }
        return listFuncsResponse.result(ListFuncsResponse::class)
    }

}