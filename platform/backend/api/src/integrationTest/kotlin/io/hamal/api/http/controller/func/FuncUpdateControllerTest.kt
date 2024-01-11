package io.hamal.api.http.controller.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.*
import io.hamal.repository.api.FlowCmdRepository.CreateCmd
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FuncUpdateControllerTest : FuncBaseControllerTest() {

    @Test
    fun `Tries to update func which does not exists`() {
        val getFuncResponse = httpTemplate.patch("/v1/funcs/33333333")
            .body(
                ApiFuncUpdateRequest(
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
                req = ApiFuncCreateRequest(
                    name = FuncName("created-name"),
                    inputs = FuncInputs(HotObject.builder().set("hamal", "createdInputs").build()),
                    code = CodeValue("createdCode")
                )
            )
        )

        val updateFuncResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", func.funcId)
            .body(
                ApiFuncUpdateRequest(
                    name = FuncName("updated-name"),
                    inputs = FuncInputs(HotObject.builder().set("hamal", "updatedInputs").build()),
                    code = CodeValue("updatedCode")
                )
            )
            .execute()

        assertThat(updateFuncResponse.statusCode, equalTo(Accepted))
        require(updateFuncResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateFuncResponse.result(ApiFuncUpdateRequested::class)
        awaitCompleted(submittedReq)

        val funcId = submittedReq.funcId

        with(getFunc(funcId)) {
            assertThat(id, equalTo(funcId))
            assertThat(flow.name, equalTo(FlowName("createdFlow")))
            assertThat(name, equalTo(FuncName("updated-name")))
            assertThat(inputs, equalTo(FuncInputs(HotObject.builder().set("hamal", "updatedInputs").build())))

            assertThat(code.version, equalTo(CodeVersion(2)))
            assertThat(code.value, equalTo(CodeValue("updatedCode")))

            assertThat(deployment.version, equalTo(CodeVersion(1)))
            assertThat(deployment.value, equalTo(CodeValue("createdCode")))
        }
    }

    @Test
    fun `Updates func without updating values`() {
        val funcId = createFuncInFlow(
            FuncName("created-name"),
            CodeValue("createdCode")
        ).funcId

        val updateFuncResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .body(
                ApiFuncUpdateRequest(
                    name = null,
                    inputs = null,
                    code = null
                )
            )
            .execute()

        assertThat(updateFuncResponse.statusCode, equalTo(Accepted))
        require(updateFuncResponse is HttpSuccessResponse) { "request was not successful" }

        val req = updateFuncResponse.result(ApiFuncUpdateRequested::class)
        awaitCompleted(req)

        with(getFunc(funcId)) {
            assertThat(id, equalTo(funcId))
            assertThat(flow.name, equalTo(FlowName("createdFlow")))
            assertThat(name, equalTo(FuncName("created-name")))
            assertThat(inputs, equalTo(FuncInputs(HotObject.builder().set("hamal", "createdInputs").build())))

            assertThat(code.version, equalTo(CodeVersion(1)))
            assertThat(code.value, equalTo(CodeValue("createdCode")))

            assertThat(deployment.version, equalTo(CodeVersion(1)))
            assertThat(deployment.value, equalTo(CodeValue("createdCode")))
        }
    }

    @Test
    fun `Does not increment code version if req code is null`() {
        val funcId = createFuncInFlow(
            FuncName("created-name"),
            CodeValue("createdCode")
        ).funcId

        val updateFuncResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .body(
                ApiFuncUpdateRequest(name = FuncName("updated-name"), code = null)
            )
            .execute()

        assertThat(updateFuncResponse.statusCode, equalTo(Accepted))
        require(updateFuncResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateFuncResponse.result(ApiFuncUpdateRequested::class)
        awaitCompleted(submittedReq)

        with(getFunc(funcId)) {
            assertThat(name, equalTo(FuncName("updated-name")))
            assertThat(flow.name, equalTo(FlowName("createdFlow")))

            assertThat(code.version, equalTo(CodeVersion(1)))
            assertThat(code.value, equalTo(CodeValue("createdCode")))

            assertThat(deployment.version, equalTo(CodeVersion(1)))
            assertThat(deployment.value, equalTo(CodeValue("createdCode")))
        }
    }

    @Test
    fun `Does not increment code version if req code is equal to existing`() {
        val funcId = createFuncInFlow(
            FuncName("func-1"),
            CodeValue("createdCode")
        ).funcId

        val updateFuncResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .body(
                ApiFuncUpdateRequest(
                    name = FuncName("updated-name"),
                    inputs = FuncInputs(HotObject.builder().set("hamal", "updatedInputs").build()),
                    code = CodeValue("createdCode")
                )
            )
            .execute()

        assertThat(updateFuncResponse.statusCode, equalTo(Accepted))
        require(updateFuncResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateFuncResponse.result(ApiFuncUpdateRequested::class)
        awaitCompleted(submittedReq)

        with(getFunc(funcId)) {
            assertThat(flow.name, equalTo(FlowName("createdFlow")))
            assertThat(name, equalTo(FuncName("updated-name")))
            assertThat(inputs, equalTo(FuncInputs(HotObject.builder().set("hamal", "updatedInputs").build())))

            assertThat(code.version, equalTo(CodeVersion(1)))
            assertThat(code.value, equalTo(CodeValue("createdCode")))

            assertThat(deployment.version, equalTo(CodeVersion(1)))
            assertThat(deployment.value, equalTo(CodeValue("createdCode")))
        }
    }

    private fun createFuncInFlow(name: FuncName, code: CodeValue): ApiFuncCreateRequested {
        val createdFlow = flowCmdRepository.create(
            CreateCmd(
                id = CmdGen(),
                flowId = FlowId(2),
                groupId = testGroup.id,
                name = FlowName("createdFlow"),
                inputs = FlowInputs()
            )
        )

        return awaitCompleted(
            createFunc(
                flowId = createdFlow.id,
                req = ApiFuncCreateRequest(
                    name = name,
                    inputs = FuncInputs(HotObject.builder().set("hamal", "createdInputs").build()),
                    code = code
                )
            )
        )
    }
}

