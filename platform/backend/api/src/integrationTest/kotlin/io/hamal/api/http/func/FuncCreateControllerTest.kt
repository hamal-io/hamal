package io.hamal.api.http.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiCreateFuncReq
import io.hamal.lib.sdk.api.ApiError
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
            ApiCreateFuncReq(
                name = FuncName("test-func"),
                inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
                code = CodeValue("13 + 37")
            )
        )
        awaitCompleted(result.reqId)

        val func = funcQueryRepository.get(result.id(::FuncId))
        with(func) {
            assertThat(name, equalTo(FuncName("test-func")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))

            val namespace = namespaceQueryRepository.get(namespaceId)
            assertThat(namespace.name, equalTo(NamespaceName("hamal")))
        }

        with(codeQueryRepository.get(func.code.id)) {
            assertThat(value, equalTo(CodeValue("13 + 37")))
            assertThat(version, equalTo(CodeVersion(1)))
        }

    }


    @Test
    fun `Create func with namespace id`() {
        val namespace = namespaceCmdRepository.create(
            CreateCmd(
                id = CmdId(1),
                namespaceId = NamespaceId(2345),
                groupId = testGroup.id,
                name = NamespaceName("hamal::name::space"),
                inputs = NamespaceInputs()
            )
        )

        val result = createFunc(
            namespaceId = namespace.id,
            req = ApiCreateFuncReq(
                name = FuncName("test-func"),
                inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
                code = CodeValue("13 + 37")
            )
        )
        awaitCompleted(result.reqId)

        val func = funcQueryRepository.get(result.id(::FuncId))

        with(func) {
            assertThat(name, equalTo(FuncName("test-func")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))

            namespaceQueryRepository.get(namespaceId).let {
                assertThat(it.id, equalTo(namespace.id))
                assertThat(it.name, equalTo(NamespaceName("hamal::name::space")))
            }
        }

        with(codeQueryRepository.get(func.code.id)) {
            assertThat(value, equalTo(CodeValue("13 + 37")))
            assertThat(version, equalTo(CodeVersion(1)))
        }
    }

    @Test
    fun `Tries to create func with namespace which does not exist`() {

        val response = httpTemplate.post("/v1/namespaces/12345/funcs")
            .body(
                ApiCreateFuncReq(
                    name = FuncName("test-func"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
                    code = CodeValue("13 + 37")
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Namespace not found"))

        assertThat(listFuncs().funcs, empty())
    }
}