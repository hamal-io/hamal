package io.hamal.api.http.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiCreateFuncReq
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import io.hamal.lib.sdk.api.ApiUpdateFuncReq
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FuncUpdateControllerTest : FuncBaseControllerTest() {

    @Test
    fun `Tries to update func which does not exists`() {
        val getFuncResponse = httpTemplate.patch("/v1/funcs/33333333")
            .body(
                ApiUpdateFuncReq(
                    name = FuncName("update"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
            .execute()

        assertThat(getFuncResponse.statusCode, equalTo(NotFound))
        require(getFuncResponse is ErrorHttpResponse) { "request was successful" }

        val error = getFuncResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))
    }

    @Test
    fun `Updates func`() {
        val createdNamespace = namespaceCmdRepository.create(
            CreateCmd(
                id = CmdId(2),
                namespaceId = NamespaceId(2),
                groupId = testGroup.id,
                name = NamespaceName("createdNamespace"),
                inputs = NamespaceInputs()
            )
        )

        val func = awaitCompleted(
            createFunc(
                namespaceId = createdNamespace.id,
                req = ApiCreateFuncReq(
                    name = FuncName("createdName"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs")))),
                    code = CodeValue("createdCode")
                )
            )
        )

        val updateFuncResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", func.id)
            .body(
                ApiUpdateFuncReq(
                    name = FuncName("updatedName"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs")))),
                    code = CodeValue("updatedCode")
                )
            )
            .execute()

        assertThat(updateFuncResponse.statusCode, equalTo(Accepted))
        require(updateFuncResponse is SuccessHttpResponse) { "request was not successful" }

        val submittedReq = updateFuncResponse.result(ApiSubmittedReqWithId::class)
        awaitCompleted(submittedReq)

        val funcId = submittedReq.id(::FuncId)

        with(getFunc(funcId)) {
            assertThat(id, equalTo(funcId))
            assertThat(namespace.name, equalTo(NamespaceName("createdNamespace")))
            assertThat(name, equalTo(FuncName("updatedName")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs"))))))

            assertThat(code.version, equalTo(CodeVersion(2)))
            assertThat(code.value, equalTo(CodeValue("updatedCode")))
        }
    }

    @Test
    fun `Updates func without updating values`() {
        val createdNamespace = namespaceCmdRepository.create(
            CreateCmd(
                id = CmdId(2),
                namespaceId = NamespaceId(2),
                groupId = testGroup.id,
                name = NamespaceName("createdNamespace"),
                inputs = NamespaceInputs()
            )
        )

        val func = awaitCompleted(
            createFunc(
                namespaceId = createdNamespace.id,
                req = ApiCreateFuncReq(
                    name = FuncName("createdName"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs")))),
                    code = CodeValue("createdCode")
                )
            )
        )

        val updateFuncResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", func.id)
            .body(
                ApiUpdateFuncReq(
                    name = null,
                    inputs = null,
                    code = null
                )
            )
            .execute()
        assertThat(updateFuncResponse.statusCode, equalTo(Accepted))
        require(updateFuncResponse is SuccessHttpResponse) { "request was not successful" }

        val req = updateFuncResponse.result(ApiSubmittedReqWithId::class)
        awaitCompleted(req)
        val funcId = req.id(::FuncId)

        with(getFunc(funcId)) {
            assertThat(id, equalTo(funcId))
            assertThat(namespace.name, equalTo(NamespaceName("createdNamespace")))
            assertThat(name, equalTo(FuncName("createdName")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs"))))))

            assertThat(code.version, equalTo(CodeVersion(2)))
            assertThat(code.value, equalTo(CodeValue("createdCode")))
        }
    }
}