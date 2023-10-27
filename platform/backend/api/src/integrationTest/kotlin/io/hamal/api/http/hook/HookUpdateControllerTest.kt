package io.hamal.api.http.hook

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiCreateHookReq
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import io.hamal.lib.sdk.api.ApiUpdateHookReq
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class HookUpdateControllerTest : HookBaseControllerTest() {

    @Test
    fun `Tries to update hook which does not exists`() {
        val getHookResponse = httpTemplate.patch("/v1/hooks/33333333")
            .body(ApiUpdateHookReq(name = HookName("update")))
            .execute()

        assertThat(getHookResponse.statusCode, equalTo(NotFound))
        require(getHookResponse is ErrorHttpResponse) { "request was successful" }

        val error = getHookResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Hook not found"))
    }

    @Test
    fun `Updates hook`() {
        val createdNamespace = namespaceCmdRepository.create(
            CreateCmd(
                id = CmdId(2),
                namespaceId = NamespaceId(2),
                groupId = testGroup.id,
                name = NamespaceName("createdNamespace"),
                inputs = NamespaceInputs()
            )
        )

        val hook = awaitCompleted(
            createHook(
                req = ApiCreateHookReq(HookName("createdName")),
                namespaceId = createdNamespace.id
            )
        )

        val updateHookResponse = httpTemplate.patch("/v1/hooks/{hookId}")
            .path("hookId", hook.id)
            .body(ApiUpdateHookReq(name = HookName("updatedName")))
            .execute()

        assertThat(updateHookResponse.statusCode, equalTo(Accepted))
        require(updateHookResponse is SuccessHttpResponse) { "request was not successful" }

        val submittedReq = updateHookResponse.result(ApiSubmittedReqWithId::class)
        awaitCompleted(submittedReq)

        val hookId = submittedReq.id(::HookId)

        with(getHook(hookId)) {
            assertThat(id, equalTo(hookId))
            assertThat(namespace.name, equalTo(NamespaceName("createdNamespace")))
            assertThat(name, equalTo(HookName("updatedName")))
        }
    }

    @Test
    fun `Updates hook without updating values`() {
        val createdNamespace = namespaceCmdRepository.create(
            CreateCmd(
                id = CmdId(2),
                namespaceId = NamespaceId(2),
                groupId = testGroup.id,
                name = NamespaceName("createdNamespace"),
                inputs = NamespaceInputs()
            )
        )

        val hook = awaitCompleted(
            createHook(
                req = ApiCreateHookReq(HookName("createdName")),
                namespaceId = createdNamespace.id
            )
        )

        val updateHookResponse = httpTemplate.patch("/v1/hooks/{hookId}")
            .path("hookId", hook.id)
            .body(ApiUpdateHookReq(name = null))
            .execute()
        assertThat(updateHookResponse.statusCode, equalTo(Accepted))
        require(updateHookResponse is SuccessHttpResponse) { "request was not successful" }

        val req = updateHookResponse.result(ApiSubmittedReqWithId::class)
        awaitCompleted(req)
        val hookId = req.id(::HookId)

        with(getHook(hookId)) {
            assertThat(id, equalTo(hookId))
            assertThat(namespace.name, equalTo(NamespaceName("createdNamespace")))
            assertThat(name, equalTo(HookName("createdName")))
        }
    }
}