package io.hamal.api.http.controller.func

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.DeployMessage
import io.hamal.lib.domain.vo.FlowId
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
        req: ApiFuncCreateReq,
        flowId: FlowId = FlowId(1)
    ): ApiFuncCreateSubmitted {
        val response = httpTemplate.post("/v1/flows/{flowId}/funcs")
            .path("flowId", flowId)
            .body(req)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }
        return response.result(ApiFuncCreateSubmitted::class)
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

    fun updateFunc(funcId: FuncId, updateReq: ApiFuncUpdateReq): ApiFuncUpdateSubmitted {
        val updateResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .body(updateReq)
            .execute()

        assertThat(updateResponse.statusCode, equalTo(Accepted))
        require(updateResponse is HttpSuccessResponse) { "request was not successful" }
        return updateResponse.result(ApiFuncUpdateSubmitted::class)
    }

    fun deployVersion(funcId: FuncId, version: CodeVersion): ApiFuncDeploySubmitted {
        val deployResponse = httpTemplate.post("/v1/funcs/{funcId}/deploy/{version}")
            .path("funcId", funcId)
            .path("version", version.value.toString())
            .execute()

        assertThat(deployResponse.statusCode, equalTo(Accepted))
        require(deployResponse is HttpSuccessResponse) { "request was not successful" }
        return deployResponse.result(ApiFuncDeploySubmitted::class)
    }

    fun deployLatestVersion(funcId: FuncId): ApiFuncDeployLatestSubmitted {
        val deployResponse = httpTemplate.post("/v1/funcs/{funcId}/deploy/latest")
            .path("funcId", funcId)
            .execute()

        assertThat(deployResponse.statusCode, equalTo(Accepted))
        require(deployResponse is HttpSuccessResponse) { "request was not successful" }
        return deployResponse.result(ApiFuncDeployLatestSubmitted::class)
    }

    fun deployLatestVersionWithMessage(funcId: FuncId, deployMessage: DeployMessage): ApiFuncDeployLatestSubmitted {
        val deployResponse = httpTemplate.post("/v1/funcs/{funcId}/deploy/latest")
            .path("funcId", funcId)
            .body(deployMessage)
            .execute()

        assertThat(deployResponse.statusCode, equalTo(Accepted))
        require(deployResponse is HttpSuccessResponse) { "request was not successful" }
        return deployResponse.result(ApiFuncDeployLatestSubmitted::class)
    }
}