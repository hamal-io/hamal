package io.hamal.api.http.controller.func

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class FuncBaseControllerTest : BaseControllerTest() {

    fun createFunc(
        req: ApiFuncCreateRequest,
        namespaceId: NamespaceId = NamespaceId(1)
    ): ApiFuncCreateRequested {
        val response = httpTemplate.post("/v1/namespaces/{namespaceId}/funcs")
            .path("namespaceId", namespaceId)
            .body(req)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }
        return response.result(ApiFuncCreateRequested::class)
    }

    fun listFuncs(): ApiFuncList {
        val listFuncsResponse = httpTemplate.get("/v1/funcs")
            .parameter("group_ids", testGroup.id)
            .execute()

        assertThat(listFuncsResponse.statusCode, equalTo(Ok))
        require(listFuncsResponse is HttpSuccessResponse) { "request was not successful" }
        return listFuncsResponse.result(ApiFuncList::class)
    }

    fun getFunc(funcId: FuncId): ApiFunc {
        val getFuncResponse = httpTemplate.get("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .execute()

        assertThat(getFuncResponse.statusCode, equalTo(Ok))
        require(getFuncResponse is HttpSuccessResponse) { "request was not successful" }
        return getFuncResponse.result(ApiFunc::class)
    }

    fun updateFunc(funcId: FuncId, updateReq: ApiFuncUpdateRequest): ApiFuncUpdateRequested {
        val updateResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .body(updateReq)
            .execute()

        assertThat(updateResponse.statusCode, equalTo(Accepted))
        require(updateResponse is HttpSuccessResponse) { "request was not successful" }
        return updateResponse.result(ApiFuncUpdateRequested::class)
    }

    fun deployFunc(funcId: FuncId, req: ApiFuncDeployRequest): ApiFuncDeployRequested {
        val deployResponse = httpTemplate.post("/v1/funcs/{funcId}/deploy")
            .path("funcId", funcId)
            .body(req)
            .execute()

        assertThat(deployResponse.statusCode, equalTo(Accepted))
        require(deployResponse is HttpSuccessResponse) { "request was not successful" }
        return deployResponse.result(ApiFuncDeployRequested::class)
    }

    fun listDeployments(funcId: FuncId): ApiFuncDeploymentList {
        val listDeployRes = httpTemplate.get("/v1/funcs/{funcId}/deployments")
            .path("funcId", funcId)
            .execute()

        assertThat(listDeployRes.statusCode, equalTo(Ok))
        require(listDeployRes is HttpSuccessResponse) { "request was not successful" }
        return listDeployRes.result(ApiFuncDeploymentList::class)
    }
}