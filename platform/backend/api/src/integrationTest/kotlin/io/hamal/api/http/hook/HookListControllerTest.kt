package io.hamal.api.http.hook

import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.HookName
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.sdk.api.ApiCreateHookReq
import io.hamal.lib.sdk.api.ApiHookList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class HookListControllerTest : HookBaseControllerTest() {
    @Test
    fun `No hooks`() {
        val result = listHooks()
        assertThat(result.hooks, empty())
    }

    @Test
    fun `Single hook`() {
        val hookId = awaitCompleted(
            createHook(
                ApiCreateHookReq(
                    namespaceId = null,
                    name = HookName("hook-one")
                )
            )
        ).id(::HookId)

        with(listHooks()) {
            assertThat(hooks, hasSize(1))
            with(hooks.first()) {
                assertThat(id, equalTo(hookId))
                assertThat(name, equalTo(HookName("hook-one")))
            }
        }
    }

    @Test
    fun `Limit hooks`() {
        awaitCompleted(
            IntRange(0, 20).map {
                createHook(
                    ApiCreateHookReq(
                        namespaceId = null,
                        name = HookName("hook-$it")
                    )
                )
            }
        )

        val listResponse = httpTemplate.get("/v1/hooks")
            .parameter("group_ids", testGroup.id)
            .parameter("limit", 12)
            .execute(ApiHookList::class)

        assertThat(listResponse.hooks, hasSize(12))

        listResponse.hooks.forEachIndexed { idx, hook ->
            assertThat(hook.name, equalTo(HookName("hook-${(20 - idx)}")))
        }
    }

    @Test
    fun `Skip and limit hooks`() {
        val requests = IntRange(0, 99).map {
            createHook(
                ApiCreateHookReq(
                    namespaceId = null,
                    name = HookName("hook-$it")
                )
            )
        }

        awaitCompleted(requests)
        val fortyNinth = requests[49]

        val listResponse = httpTemplate.get("/v1/hooks")
            .parameter("group_ids", testGroup.id)
            .parameter("after_id", fortyNinth.id)
            .parameter("limit", 1)
            .execute(ApiHookList::class)

        assertThat(listResponse.hooks, hasSize(1))

        val hook = listResponse.hooks.first()
        assertThat(hook.namespace.name, equalTo(NamespaceName("hamal")))
        assertThat(hook.name, equalTo(HookName("hook-48")))
    }
}