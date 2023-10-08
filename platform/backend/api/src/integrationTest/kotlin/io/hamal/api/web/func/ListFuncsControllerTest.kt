package io.hamal.api.web.func

import io.hamal.lib.domain.vo.*
import io.hamal.lib.sdk.api.ApiCreateFuncReq
import io.hamal.lib.sdk.api.ApiFuncList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ListFuncsControllerTest : BaseFuncControllerTest() {
    @Test
    fun `No funcs`() {
        val result = listFuncs()
        assertThat(result.funcs, empty())
    }

    @Test
    fun `Single func`() {
        val funcId = awaitCompleted(
            createFunc(
                ApiCreateFuncReq(
                    namespaceId = null,
                    name = FuncName("func-one"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
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
                    ApiCreateFuncReq(
                        namespaceId = null,
                        name = FuncName("func-$it"),
                        inputs = FuncInputs(),
                        code = CodeValue("")
                    )
                )
            }
        )

        val listResponse = httpTemplate.get("/v1/groups/{groupId}/funcs")
            .path("groupId", testGroup.id)
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
                ApiCreateFuncReq(
                    namespaceId = null,
                    name = FuncName("func-$it"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
        }

        awaitCompleted(requests)
        val fortyNinth = requests[49]

        val listResponse = httpTemplate.get("/v1/groups/{groupId}/funcs")
            .path("groupId", testGroup.id)
            .parameter("after_id", fortyNinth.id)
            .parameter("limit", 1)
            .execute(ApiFuncList::class)

        assertThat(listResponse.funcs, hasSize(1))

        val func = listResponse.funcs.first()
        assertThat(func.namespace.name, equalTo(NamespaceName("hamal")))
        assertThat(func.name, equalTo(FuncName("func-48")))
    }
}