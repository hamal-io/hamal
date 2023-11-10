package io.hamal.api.http.controller.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiFuncCreateReq
import io.hamal.repository.api.FlowCmdRepository.CreateCmd
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class FuncCreateControllerTest : FuncBaseControllerTest() {

    @TestFactory
    fun `Create func for default flow id`() {
        val result = createFunc(
            ApiFuncCreateReq(
                name = FuncName("test-func"),
                inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
                code = CodeValue("13 + 37")
            )
        )
        awaitCompleted(result)

        val func = funcQueryRepository.get(result.funcId)
        with(func) {
            assertThat(name, equalTo(FuncName("test-func")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))

            val flow = flowQueryRepository.get(flowId)
            assertThat(flow.name, equalTo(FlowName("hamal")))
        }

        with(codeQueryRepository.get(func.code.id)) {
            assertThat(value, equalTo(CodeValue("13 + 37")))
            assertThat(version, equalTo(CodeVersion(1)))
        }

    }

    @Test
    fun `Create func with flow id`() {
        val flow = flowCmdRepository.create(
            CreateCmd(
                id = CmdId(1),
                flowId = FlowId(2345),
                groupId = testGroup.id,
                name = FlowName("hamal::name::space"),
                inputs = FlowInputs()
            )
        )

        val result = createFunc(
            flowId = flow.id,
            req = ApiFuncCreateReq(
                name = FuncName("test-func"),
                inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
                code = CodeValue("13 + 37")
            )
        )
        awaitCompleted(result)

        val func = funcQueryRepository.get(result.funcId)

        with(func) {
            assertThat(name, equalTo(FuncName("test-func")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))

            flowQueryRepository.get(flowId).let {
                assertThat(it.id, equalTo(flow.id))
                assertThat(it.name, equalTo(FlowName("hamal::name::space")))
            }
        }

        with(codeQueryRepository.get(func.code.id)) {
            assertThat(value, equalTo(CodeValue("13 + 37")))
            assertThat(version, equalTo(CodeVersion(1)))
        }
    }

    @Test
    fun `Tries to create func with flow which does not exist`() {

        val response = httpTemplate.post("/v1/flows/12345/funcs")
            .body(
                ApiFuncCreateReq(
                    name = FuncName("test-func"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
                    code = CodeValue("13 + 37")
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is HttpErrorResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Flow not found"))

        assertThat(listFuncs().funcs, empty())
    }
}