package io.hamal.backend.instance.web.func

import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.domain.ApiFuncList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ListFuncRouteTest : BaseFuncRouteTest() {
    @Test
    fun `No funcs`() {
        val result = listFuncs()
        assertThat(result.funcs, empty())
    }

    @Test
    fun `Single func`() {
        val funcId = awaitCompleted(
            createFunc(
                CreateFuncReq(
                    namespaceId = null,
                    name = FuncName("func-one"),
                    inputs = FuncInputs(),
                    code = CodeType("")
                )
            )
        ).id(::FuncId)

        with(listFuncs()) {
            assertThat(funcs, hasSize(1))
            with(funcs.first()) {
                assertThat(id, equalTo(funcId))
                assertThat(name, equalTo(FuncName("func-one")))
            }
        }
    }

    @Test
    fun `Limit funcs`() {
        awaitCompleted(
            IntRange(0, 20).map {
                createFunc(
                    CreateFuncReq(
                        namespaceId = null,
                        name = FuncName("func-$it"),
                        inputs = FuncInputs(),
                        code = CodeType("")
                    )
                )
            }
        )

        val listResponse = httpTemplate.get("/v1/funcs")
            .parameter("limit", 12)
            .execute(ApiFuncList::class)

        assertThat(listResponse.funcs, hasSize(12))

        listResponse.funcs.forEachIndexed { idx, func ->
            assertThat(func.name, equalTo(FuncName("func-${(20 - idx)}")))
        }
    }

    @Test
    fun `Skip and limit funcs`() {
        val requests = IntRange(0, 99).map {
            createFunc(
                CreateFuncReq(
                    namespaceId = null,
                    name = FuncName("func-$it"),
                    inputs = FuncInputs(),
                    code = CodeType("")
                )
            )
        }

        awaitCompleted(requests)
        val fortyNinth = requests[49]

        val listResponse = httpTemplate.get("/v1/funcs")
            .parameter("after_id", fortyNinth.id)
            .parameter("limit", 1)
            .execute(ApiFuncList::class)

        assertThat(listResponse.funcs, hasSize(1))

        val func = listResponse.funcs.first()
        assertThat(func.namespace.name, equalTo(NamespaceName("hamal")))
        assertThat(func.name, equalTo(FuncName("func-48")))
    }
}