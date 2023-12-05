package io.hamal.api.http.controller.func

import io.hamal.lib.domain.vo.*
import io.hamal.lib.sdk.api.ApiFuncCreateReq
import io.hamal.lib.sdk.api.ApiFuncDeployReq
import io.hamal.lib.sdk.api.ApiFuncList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class FuncListControllerTest : FuncBaseControllerTest() {
    @Test
    fun `No funcs`() {
        val result = listFuncs()
        assertThat(result.funcs, empty())
    }

    @Test
    fun `Single func`() {
        val funcId = awaitCompleted(
            createFunc(
                ApiFuncCreateReq(
                    name = FuncName("func-one"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
        ).funcId

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
                    ApiFuncCreateReq(
                        name = FuncName("func-$it"),
                        inputs = FuncInputs(),
                        code = CodeValue("")
                    )
                )
            }
        )

        val listResponse = httpTemplate.get("/v1/funcs")
            .parameter("group_ids", testGroup.id)
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
                ApiFuncCreateReq(
                    name = FuncName("func-$it"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
        }

        awaitCompleted(requests)
        val fortyNinth = requests[49]

        val listResponse = httpTemplate.get("/v1/funcs")
            .parameter("group_ids", testGroup.id)
            .parameter("after_id", fortyNinth.funcId)
            .parameter("limit", 1)
            .execute(ApiFuncList::class)

        assertThat(listResponse.funcs, hasSize(1))

        val func = listResponse.funcs.first()
        assertThat(func.flow.name, equalTo(FlowName("hamal")))
        assertThat(func.name, equalTo(FuncName("func-48")))
    }

    @Test
    fun `Get a list of deployments`() {
        val funcId = awaitCompleted(
            createFunc(
                ApiFuncCreateReq(
                    name = FuncName("func-1"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
        ).funcId

        repeat(20) {
            awaitCompleted(deployFunc(funcId, ApiFuncDeployReq(null, DeployMessage("deployed-${it}"))))
        }

        val deployments = listDeployments(funcId).deployments
        assertThat(deployments, hasSize(21))
        assertThat(deployments[10].message, equalTo(DeployMessage("deployed-9")))
        assertThat(deployments[10].version, equalTo(CodeVersion(1)))
    }

}