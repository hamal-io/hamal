package io.hamal.api.web.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
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

internal class UpdateFuncControllerTest : BaseFuncControllerTest() {

    @Test
    fun `Tries to update func which does not exists`() {
        val getFuncResponse = httpTemplate.put("/v1/funcs/33333333")
            .body(
                ApiUpdateFuncReq(
                    namespaceId = null,
                    name = FuncName("update"),
                    inputs = FuncInputs(),
                    code = CodeType("")
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
                id = CmdId(1),
                namespaceId = NamespaceId(1),
                groupId = testGroup.id,
                name = NamespaceName("createdNamespace"),
                inputs = NamespaceInputs()
            )
        )

        val func = awaitCompleted(
            createFunc(
                ApiCreateFuncReq(
                    namespaceId = createdNamespace.id,
                    name = FuncName("createdName"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs")))),
                    code = CodeType("createdCode")
                )
            )
        )

        val updateNamespace = namespaceCmdRepository.create(
            CreateCmd(
                id = CmdId(2),
                namespaceId = NamespaceId(2),
                groupId = testGroup.id,
                name = NamespaceName("updatedNamespace"),
                inputs = NamespaceInputs()
            )
        )

        val updateFuncResponse = httpTemplate.put("/v1/funcs/{funcId}")
            .path("funcId", func.id)
            .body(
                ApiUpdateFuncReq(
                    namespaceId = updateNamespace.id,
                    name = FuncName("updatedName"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs")))),
                    code = CodeType("updatedCode")
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
            assertThat(namespace.name, equalTo(NamespaceName("updatedNamespace")))
            assertThat(name, equalTo(FuncName("updatedName")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs"))))))
            assertThat(code, equalTo(CodeType("updatedCode")))
        }
    }

    @Test
    fun `Tries to update namespace id which does not exists`() {
        val createdNamespace = namespaceCmdRepository.create(
            CreateCmd(
                id = CmdId(1),
                namespaceId = NamespaceId(1),
                groupId = testGroup.id,
                name = NamespaceName("createdNamespace"),
                inputs = NamespaceInputs()
            )
        )

        val func = awaitCompleted(
            createFunc(
                ApiCreateFuncReq(
                    namespaceId = createdNamespace.id,
                    name = FuncName("createdName"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs")))),
                    code = CodeType("createdCode")
                )
            )
        )

        val updateFuncResponse = httpTemplate.put("/v1/funcs/{funcId}")
            .path("funcId", func.id)
            .body(ApiUpdateFuncReq(NamespaceId(12345)))
            .execute()

        assertThat(updateFuncResponse.statusCode, equalTo(NotFound))
        require(updateFuncResponse is ErrorHttpResponse) { "request was successful" }

        val error = updateFuncResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Namespace not found"))

        with(getFunc(func.id(::FuncId))) {
            assertThat(namespace.id, equalTo(createdNamespace.id))
            assertThat(namespace.name, equalTo(NamespaceName("createdNamespace")))
        }
    }

    @Test
    fun `Updates func without updating values`() {
        val createdNamespace = namespaceCmdRepository.create(
            CreateCmd(
                id = CmdId(1),
                namespaceId = NamespaceId(1),
                groupId = testGroup.id,
                name = NamespaceName("createdNamespace"),
                inputs = NamespaceInputs()
            )
        )

        val func = awaitCompleted(
            createFunc(
                ApiCreateFuncReq(
                    namespaceId = createdNamespace.id,
                    name = FuncName("createdName"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs")))),
                    code = CodeType("createdCode")
                )
            )
        )

        val updateFuncResponse = httpTemplate.put("/v1/funcs/{funcId}")
            .path("funcId", func.id)
            .body(
                ApiUpdateFuncReq(
                    namespaceId = null,
                    name = null,
                    inputs = null,
                    code = null
                )
            )
            .execute()
        assertThat(updateFuncResponse.statusCode, equalTo(Accepted))
        require(updateFuncResponse is SuccessHttpResponse) { "request was not successful" }

        val funcId = updateFuncResponse.result(ApiSubmittedReqWithId::class).id(::FuncId)

        with(getFunc(funcId)) {
            assertThat(id, equalTo(funcId))
            assertThat(namespace.name, equalTo(NamespaceName("createdNamespace")))
            assertThat(name, equalTo(FuncName("createdName")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs"))))))
            assertThat(code, equalTo(CodeType("createdCode")))
        }
    }
}