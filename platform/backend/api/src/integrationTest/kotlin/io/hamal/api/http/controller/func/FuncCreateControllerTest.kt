package io.hamal.api.http.controller.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName.Companion.FuncName
import io.hamal.lib.domain.vo.NamespaceFeatures
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName.Companion.NamespaceName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiFuncCreateRequest
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class FuncCreateControllerTest : FuncBaseControllerTest() {

    @TestFactory
    fun `Create func for default namespace id`() {
        val result = createFunc(
            ApiFuncCreateRequest(
                name = FuncName("test-func"),
                inputs = FuncInputs(HotObject.builder().set("hamal", "rocks").build()),
                code = ValueCode("13 + 37"),
                codeType = CodeType.Lua54
            )
        )
        awaitCompleted(result)

        val func = funcQueryRepository.get(result.id)
        with(func) {
            assertThat(name, equalTo(FuncName("test-func")))
            assertThat(inputs, equalTo(FuncInputs(HotObject.builder().set("hamal", "rocks").build())))

            val namespace = namespaceQueryRepository.get(namespaceId)
            assertThat(namespace.name, equalTo(NamespaceName("hamal")))
        }

        with(codeQueryRepository.get(func.code.id)) {
            assertThat(value, equalTo(ValueCode("13 + 37")))
            assertThat(version, equalTo(CodeVersion(1)))
            assertThat(type, equalTo(CodeType.Lua54))
        }

    }

    @Test
    fun `Create func with namespace id`() {
        val namespace = namespaceCmdRepository.create(
            CreateCmd(
                id = CmdId(1),
                namespaceId = NamespaceId(2345),
                workspaceId = testWorkspace.id,
                name = NamespaceName("hamal::namespace"),
                features = NamespaceFeatures.default
            )
        )

        val result = createFunc(
            namespaceId = namespace.id,
            req = ApiFuncCreateRequest(
                name = FuncName("test-func"),
                inputs = FuncInputs(HotObject.builder().set("hamal", "rocks").build()),
                code = ValueCode("13 + 37"),
                codeType = CodeType.Lua54
            )
        )
        awaitCompleted(result)

        val func = funcQueryRepository.get(result.id)

        with(func) {
            assertThat(name, equalTo(FuncName("test-func")))
            assertThat(inputs, equalTo(FuncInputs(HotObject.builder().set("hamal", "rocks").build())))

            namespaceQueryRepository.get(namespaceId).let {
                assertThat(it.id, equalTo(namespace.id))
                assertThat(it.name, equalTo(NamespaceName("hamal::namespace")))
            }
        }

        with(codeQueryRepository.get(func.code.id)) {
            assertThat(value, equalTo(ValueCode("13 + 37")))
            assertThat(version, equalTo(CodeVersion(1)))
            assertThat(type, equalTo(CodeType.Lua54))
        }
    }

    @Test
    fun `Tries to create func with namespace which does not exist`() {

        val response = httpTemplate.post("/v1/namespaces/12345/funcs")
            .body(
                ApiFuncCreateRequest(
                    name = FuncName("test-func"),
                    inputs = FuncInputs(HotObject.builder().set("hamal", "rocks").build()),
                    code = ValueCode("13 + 37"),
                    codeType = CodeType.Lua54
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is HttpErrorResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Namespace not found"))

        assertThat(listFuncs().funcs, empty())
    }
}