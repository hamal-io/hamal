package io.hamal.api.http.func

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiFunc
import io.hamal.lib.sdk.api.ApiFuncCreateReq
import io.hamal.lib.sdk.api.ApiFuncList
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

@Suppress("UNCHECKED_CAST")
internal sealed class FuncBaseControllerTest : BaseControllerTest() {

    fun createFunc(
        req: ApiFuncCreateReq,
        namespaceId: NamespaceId = NamespaceId(1)
    ): ApiSubmittedReqImpl<FuncId> {
        val response = httpTemplate.post("/v1/namespaces/{namespaceId}/funcs")
            .path("namespaceId", namespaceId)
            .body(req)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(ApiSubmittedReqImpl::class) as ApiSubmittedReqImpl<FuncId>
    }

    fun listFuncs(): ApiFuncList {
        val listFuncsResponse = httpTemplate.get("/v1/funcs")
            .parameter("group_ids", testGroup.id)
            .execute()

        assertThat(listFuncsResponse.statusCode, equalTo(Ok))
        require(listFuncsResponse is SuccessHttpResponse) { "request was not successful" }
        return listFuncsResponse.result(ApiFuncList::class)
    }

    fun getFunc(funcId: FuncId): ApiFunc {
        val getFuncResponse = httpTemplate.get("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .execute()

        assertThat(getFuncResponse.statusCode, equalTo(Ok))
        require(getFuncResponse is SuccessHttpResponse) { "request was not successful" }
        return getFuncResponse.result(ApiFunc::class)
    }
}