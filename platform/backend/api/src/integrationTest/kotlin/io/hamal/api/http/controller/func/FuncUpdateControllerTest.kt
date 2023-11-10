package io.hamal.api.http.controller.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiFuncCreateReq
import io.hamal.lib.sdk.api.ApiFuncUpdateReq
import io.hamal.lib.sdk.api.ApiFuncUpdateSubmitted
import io.hamal.repository.api.FlowCmdRepository.CreateCmd
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FuncUpdateControllerTest : FuncBaseControllerTest() {

    @Test
    fun `Tries to update func which does not exists`() {
        val getFuncResponse = httpTemplate.patch("/v1/funcs/33333333")
            .body(
                ApiFuncUpdateReq(
                    name = FuncName("update"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
            .execute()

        assertThat(getFuncResponse.statusCode, equalTo(NotFound))
        require(getFuncResponse is HttpErrorResponse) { "request was successful" }

        val error = getFuncResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))
    }

    @Test
    fun `Updates func`() {
        val createdFlow = flowCmdRepository.create(
            CreateCmd(
                id = CmdId(2),
                flowId = FlowId(2),
                groupId = testGroup.id,
                name = FlowName("createdFlow"),
                inputs = FlowInputs()
            )
        )

        val func = awaitCompleted(
            createFunc(
                flowId = createdFlow.id,
                req = ApiFuncCreateReq(
                    name = FuncName("createdName"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs")))),
                    code = CodeValue("createdCode")
                )
            )
        )

        val updateFuncResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", func.funcId)
            .body(
                ApiFuncUpdateReq(
                    name = FuncName("updatedName"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs")))),
                    code = CodeValue("updatedCode")
                )
            )
            .execute()

        assertThat(updateFuncResponse.statusCode, equalTo(Accepted))
        require(updateFuncResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateFuncResponse.result(ApiFuncUpdateSubmitted::class)
        awaitCompleted(submittedReq)

        val funcId = submittedReq.funcId

        with(getFunc(funcId)) {
            assertThat(id, equalTo(funcId))
            assertThat(flow.name, equalTo(FlowName("createdFlow")))
            assertThat(name, equalTo(FuncName("updatedName")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs"))))))

            assertThat(code.current.version, equalTo(CodeVersion(2)))
            assertThat(code.current.value, equalTo(CodeValue("updatedCode")))

            assertThat(code.deployed.version, equalTo(CodeVersion(1)))
            assertThat(code.deployed.value, equalTo(CodeValue("createdCode")))
        }
    }

    @Test
    fun `Updates func without updating values`() {
        val createdFlow = flowCmdRepository.create(
            CreateCmd(
                id = CmdId(2),
                flowId = FlowId(2),
                groupId = testGroup.id,
                name = FlowName("createdFlow"),
                inputs = FlowInputs()
            )
        )

        val func = awaitCompleted(
            createFunc(
                flowId = createdFlow.id,
                req = ApiFuncCreateReq(
                    name = FuncName("createdName"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs")))),
                    code = CodeValue("createdCode")
                )
            )
        )

        val updateFuncResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", func.funcId)
            .body(
                ApiFuncUpdateReq(
                    name = null,
                    inputs = null,
                    code = null
                )
            )
            .execute()
        assertThat(updateFuncResponse.statusCode, equalTo(Accepted))
        require(updateFuncResponse is HttpSuccessResponse) { "request was not successful" }

        val req = updateFuncResponse.result(ApiFuncUpdateSubmitted::class)
        awaitCompleted(req)
        val funcId = req.funcId

        with(getFunc(funcId)) {
            assertThat(id, equalTo(funcId))
            assertThat(flow.name, equalTo(FlowName("createdFlow")))
            assertThat(name, equalTo(FuncName("createdName")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs"))))))

            // assertThat(code.current.version, equalTo(CodeVersion(1))) //FIXME with core-73
            assertThat(code.current.value, equalTo(CodeValue("createdCode")))

            assertThat(code.deployed.version, equalTo(CodeVersion(1)))
            assertThat(code.deployed.value, equalTo(CodeValue("createdCode")))
        }
    }
}