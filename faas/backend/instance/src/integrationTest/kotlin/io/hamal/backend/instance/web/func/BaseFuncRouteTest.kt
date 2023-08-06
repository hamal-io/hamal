package io.hamal.backend.instance.web.func

import io.hamal.backend.instance.web.BaseRouteTest
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.req.SubmittedCreateFuncReq
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.domain.ListFuncsResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseFuncRouteTest : BaseRouteTest() {
    fun createFunc(req: CreateFuncReq): SubmittedCreateFuncReq {
        val response = httpTemplate.post("/v1/funcs").body(req).execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(SubmittedCreateFuncReq::class)
    }

    fun listFuncs(): ListFuncsResponse {
        val listFuncsResponse = httpTemplate.get("/v1/funcs").execute()
        assertThat(listFuncsResponse.statusCode, equalTo(Ok))
        require(listFuncsResponse is SuccessHttpResponse) { "request was not successful" }
        return listFuncsResponse.result(ListFuncsResponse::class)
    }

    fun getFunc(funcId: FuncId): Func {
        val getFuncResponse = httpTemplate.get("/v1/funcs/${funcId.value.value}").execute()
        assertThat(getFuncResponse.statusCode, equalTo(Ok))
        require(getFuncResponse is SuccessHttpResponse) { "request was not successful" }
        return getFuncResponse.result(Func::class)
    }
}