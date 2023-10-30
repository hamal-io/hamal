package io.hamal.api.http.func

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
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
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
            assertThat(namespace.name, equalTo(NamespaceName("createdNamespace")))
            assertThat(name, equalTo(FuncName("createdName")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs"))))))

            assertThat(code.version, equalTo(CodeVersion(2)))
            assertThat(code.value, equalTo(CodeValue("createdCode")))
        }
    }

    // FIXME-53
//    @Nested
//    inner class FuncDeployedCodeTest {
//        @Test
//        fun `Updates deployed version`() {
//            val func = awaitCompleted(
//                createFunc(
//                    ApiFuncCreateReq(
//                        name = FuncName("Func-base"),
//                        inputs = FuncInputs(),
//                        code = CodeValue("40 + 2")
//                    )
//                )
//            )
//
//            awaitCompleted(updateDeployedVersion(func.funcId, CodeVersion(123)))
//
//            with(funcQueryRepository.get(func.funcId)) {
//                assertThat(code.deployedVersion, equalTo(CodeVersion(123)))
//                assertThat(name, equalTo(FuncName("Func-base")))
//                assertThat(code.version, equalTo(CodeVersion(1)))
//            }
//        }
//
//        @Test
//        fun `Tries to update deployed version that does not exist`() {
//            val res = httpTemplate.post("/v1/funcs/1234/deploy/25")
//                .execute()
//
//
//            assertThat(res.statusCode, equalTo(NotFound))
//            require(res is HttpErrorResponse) { "request was successful" }
//
//            val error = res.error(ApiError::class)
//            assertThat(error.message, equalTo("Func not found"))
//        }
//    }


}