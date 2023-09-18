package io.hamal.admin.web.func

import io.hamal.admin.web.BaseControllerTest
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.admin.AdminCreateFuncReq
import io.hamal.lib.sdk.admin.AdminFunc
import io.hamal.lib.sdk.admin.AdminFuncList
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseFuncControllerTest : BaseControllerTest() {
    fun createFunc(req: AdminCreateFuncReq): AdminSubmittedReqWithId {
        val response = httpTemplate.post("/v1/groups/{groupId}/funcs")
            .path("groupId", testGroup.id)
            .body(req)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(AdminSubmittedReqWithId::class)
    }

    fun listFuncs(): AdminFuncList {
        val listFuncsResponse = httpTemplate.get("/v1/funcs")
            .parameter("group_ids", testGroup.id)
            .execute()

        assertThat(listFuncsResponse.statusCode, equalTo(Ok))
        require(listFuncsResponse is SuccessHttpResponse) { "request was not successful" }
        return listFuncsResponse.result(AdminFuncList::class)
    }

    fun getFunc(funcId: FuncId): AdminFunc {
        val getFuncResponse = httpTemplate.get("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .execute()

        assertThat(getFuncResponse.statusCode, equalTo(Ok))
        require(getFuncResponse is SuccessHttpResponse) { "request was not successful" }
        return getFuncResponse.result(AdminFunc::class)
    }
}