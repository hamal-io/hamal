package io.hamal.backend.web.func

import io.hamal.backend.web.BaseRouteTest
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.hub.HubFunc
import io.hamal.lib.sdk.hub.HubFuncList
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseFuncRouteTest : BaseRouteTest() {
    fun createFunc(req: CreateFuncReq): HubSubmittedReqWithId {
        val response = httpTemplate.post("/v1/groups/{groupId}/funcs")
            .path("groupId", testGroup.id)
            .body(req)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(HubSubmittedReqWithId::class)
    }

    fun listFuncs(): HubFuncList {
        val listFuncsResponse = httpTemplate.get("/v1/groups/{groupId}/funcs")
            .path("groupId", testGroup.id)
            .execute()

        assertThat(listFuncsResponse.statusCode, equalTo(Ok))
        require(listFuncsResponse is SuccessHttpResponse) { "request was not successful" }
        return listFuncsResponse.result(HubFuncList::class)
    }

    fun getFunc(funcId: FuncId): HubFunc {
        val getFuncResponse = httpTemplate.get("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .execute()

        assertThat(getFuncResponse.statusCode, equalTo(Ok))
        require(getFuncResponse is SuccessHttpResponse) { "request was not successful" }
        return getFuncResponse.result(HubFunc::class)
    }
}