package io.hamal.api.http.hook

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiCreateHookReq
import io.hamal.lib.sdk.api.ApiError
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class HookCreateControllerTest : HookBaseControllerTest() {

    @Test
    fun `Create hook with default namespace id`() {
        val result = createHook(
            req = ApiCreateHookReq(
                name = HookName("test-hook"),
            ),
            namespaceId = NamespaceId(1)
        )
        awaitCompleted(result.reqId)

        val hook = hookQueryRepository.get(result.id(::HookId))
        with(hook) {
            assertThat(name, equalTo(HookName("test-hook")))

            val namespace = namespaceQueryRepository.get(namespaceId)
            assertThat(namespace.name, equalTo(NamespaceName("hamal")))
        }

    }


    @Test
    fun `Create hook with namespace id`() {
        val namespace = namespaceCmdRepository.create(
            CreateCmd(
                id = CmdId(1),
                namespaceId = NamespaceId(2345),
                groupId = testGroup.id,
                name = NamespaceName("hamal::name::space"),
                inputs = NamespaceInputs()
            )
        )

        val result = createHook(
            req = ApiCreateHookReq(HookName("test-hook")),
            namespaceId = namespace.id
        )
        awaitCompleted(result.reqId)

        val hook = hookQueryRepository.get(result.id(::HookId))

        with(hook) {
            assertThat(name, equalTo(HookName("test-hook")))

            namespaceQueryRepository.get(namespaceId).let {
                assertThat(it.id, equalTo(namespace.id))
                assertThat(it.name, equalTo(NamespaceName("hamal::name::space")))
            }
        }
    }

    @Test
    fun `Tries to create hook with namespace which does not exist`() {

        val response = httpTemplate.post("/v1/namespaces/12345/hooks")
            .path("groupId", testGroup.id)
            .body(ApiCreateHookReq(HookName("test-hook")))
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Namespace not found"))

        assertThat(listHooks().hooks, empty())
    }
}