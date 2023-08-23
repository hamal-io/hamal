package io.hamal.backend.instance.web.func

import io.hamal.backend.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.DepTableType
import io.hamal.lib.sdk.domain.ApiError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class CreateFuncRouteTest : BaseFuncRouteTest() {
    @Test
    fun `Create func without namespace id`() {
        val result = createFunc(
            CreateFuncReq(
                name = FuncName("test-func"),
                namespaceId = null,
                inputs = FuncInputs(DepTableType(StringType("hamal") to StringType("rocks"))),
                code = CodeType("13 + 37")
            )
        )
        awaitCompleted(result.reqId)

        with(funcQueryRepository.get(result.id(::FuncId))) {
            assertThat(name, equalTo(FuncName("test-func")))
            assertThat(inputs, equalTo(FuncInputs(DepTableType(StringType("hamal") to StringType("rocks")))))
            assertThat(code, equalTo(CodeType("13 + 37")))

            val namespace = namespaceQueryRepository.get(namespaceId)
            assertThat(namespace.name, equalTo(NamespaceName("hamal")))
        }
    }


    @Test
    fun `Create func with namespace id`() {
        val namespace = namespaceCmdRepository.create(
            CreateCmd(
                id = CmdId(1),
                namespaceId = NamespaceId(2345),
                name = NamespaceName("hamal::name::space"),
                inputs = NamespaceInputs()
            )
        )

        val result = createFunc(
            CreateFuncReq(
                name = FuncName("test-func"),
                namespaceId = namespace.id,
                inputs = FuncInputs(DepTableType(StringType("hamal") to StringType("rocks"))),
                code = CodeType("13 + 37")
            )
        )
        awaitCompleted(result.reqId)

        with(funcQueryRepository.get(result.id(::FuncId))) {
            assertThat(name, equalTo(FuncName("test-func")))
            assertThat(inputs, equalTo(FuncInputs(DepTableType(StringType("hamal") to StringType("rocks")))))
            assertThat(code, equalTo(CodeType("13 + 37")))

            namespaceQueryRepository.get(namespaceId).let {
                assertThat(it.id, equalTo(namespace.id))
                assertThat(it.name, equalTo(NamespaceName("hamal::name::space")))
            }
        }
    }

    @Test
    fun `Tries to create func with namespace which does not exist`() {

        val response = httpTemplate.post("/v1/funcs")
            .body(
                CreateFuncReq(
                    name = FuncName("test-func"),
                    namespaceId = NamespaceId(12345),
                    inputs = FuncInputs(DepTableType(StringType("hamal") to StringType("rocks"))),
                    code = CodeType("13 + 37")
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