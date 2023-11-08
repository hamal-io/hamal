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
import io.hamal.lib.sdk.api.*
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

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

            assertThat(code.current.version, equalTo(CodeVersion(2)))
            assertThat(code.current.value, equalTo(CodeValue("updatedCode")))

            assertThat(code.deployed.version, equalTo(CodeVersion(1)))
            assertThat(code.deployed.value, equalTo(CodeValue("createdCode")))
        }
    }

    @Test
    fun `Updates func without updating values`() {
        val funcId = createFuncInNamespace(
            FuncName("createdName"),
            CodeValue("createdCode")
        ).funcId

        val updateFuncResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", funcId)
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

        with(getFunc(funcId)) {
            assertThat(id, equalTo(funcId))
            assertThat(namespace.name, equalTo(NamespaceName("createdNamespace")))
            assertThat(name, equalTo(FuncName("createdName")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs"))))))

            assertThat(code.current.version, equalTo(CodeVersion(1)))
            assertThat(code.current.value, equalTo(CodeValue("createdCode")))

            assertThat(code.deployed.version, equalTo(CodeVersion(1)))
            assertThat(code.deployed.value, equalTo(CodeValue("createdCode")))
        }
    }

    @Test
    fun `Does not increment code version if req code is null`() {
        val funcId = createFuncInNamespace(
            FuncName("createdName"),
            CodeValue("createdCode")
        ).funcId

        val updateFuncResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .body(
                ApiFuncUpdateReq(name = FuncName("updatedName"))
            )
            .execute()

        assertThat(updateFuncResponse.statusCode, equalTo(Accepted))
        require(updateFuncResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateFuncResponse.result(ApiFuncUpdateSubmitted::class)
        awaitCompleted(submittedReq)

        with(getFunc(funcId)) {
            assertThat(name, equalTo(FuncName("updatedName")))
            assertThat(namespace.name, equalTo(NamespaceName("createdNamespace")))

            assertThat(code.current.version, equalTo(CodeVersion(1)))
            assertThat(code.deployed.version, equalTo(CodeVersion(1)))

            assertThat(code.current.value, equalTo(CodeValue("createdCode")))
            assertThat(code.deployed.value, equalTo(CodeValue("createdCode")))
        }
    }

    @Test
    fun `Does not increment code version if req code is equal to existing`() {
        val funcId = createFuncInNamespace(
            FuncName("func-1"),
            CodeValue("createdCode")
        ).funcId

        val updateFuncResponse = httpTemplate.patch("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .body(
                ApiFuncUpdateReq(
                    name = FuncName("updatedName"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs")))),
                    code = CodeValue("createdCode")
                )
            )
            .execute()

        assertThat(updateFuncResponse.statusCode, equalTo(Accepted))
        require(updateFuncResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateFuncResponse.result(ApiFuncUpdateSubmitted::class)
        awaitCompleted(submittedReq)

        with(getFunc(funcId)) {
            assertThat(namespace.name, equalTo(NamespaceName("createdNamespace")))
            assertThat(name, equalTo(FuncName("updatedName")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs"))))))

            assertThat(code.current.version, equalTo(CodeVersion(1)))
            assertThat(code.deployed.version, equalTo(CodeVersion(1)))

            assertThat(code.current.value, equalTo(CodeValue("createdCode")))
            assertThat(code.deployed.value, equalTo(CodeValue("createdCode")))
        }
    }

    private fun createFuncInNamespace(name: FuncName, code: CodeValue): ApiFuncCreateSubmitted {
        val createdNamespace = namespaceCmdRepository.create(
            CreateCmd(
                id = CmdGen(),
                namespaceId = NamespaceId(2),
                groupId = testGroup.id,
                name = NamespaceName("createdNamespace"),
                inputs = NamespaceInputs()
            )
        )

        return awaitCompleted(
            createFunc(
                namespaceId = createdNamespace.id,
                req = ApiFuncCreateReq(
                    name = name,
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs")))),
                    code = code
                )
            )
        )
    }


    private object CmdGen {
        private val atomicCounter = AtomicInteger(1)

        operator fun invoke(): CmdId {
            return CmdId(atomicCounter.incrementAndGet())
        }
    }
}

