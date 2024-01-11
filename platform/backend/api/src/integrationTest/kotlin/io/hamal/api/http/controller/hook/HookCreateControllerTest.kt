package io.hamal.api.http.controller.hook

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.domain.vo.HookName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiHookCreateRequest
import io.hamal.repository.api.FlowCmdRepository.CreateCmd
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class HookCreateControllerTest : HookBaseControllerTest() {

    @Test
    fun `Create hook with default flow id`() {
        val result = createHook(
            req = ApiHookCreateRequest(
                name = HookName("test-hook"),
            ),
            flowId = FlowId(1)
        )
        awaitCompleted(result)

        val hook = hookQueryRepository.get(result.hookId)
        with(hook) {
            assertThat(name, equalTo(HookName("test-hook")))

            val flow = flowQueryRepository.get(flowId)
            assertThat(flow.name, equalTo(FlowName("hamal")))
        }

    }


    @Test
    fun `Create hook with flow id`() {
        val flow = flowCmdRepository.create(
            CreateCmd(
                id = CmdId(1),
                flowId = FlowId(2345),
                groupId = testGroup.id,
                name = FlowName("hamal::flow"),
                inputs = FlowInputs()
            )
        )

        val result = createHook(
            req = ApiHookCreateRequest(HookName("test-hook")),
            flowId = flow.id
        )
        awaitCompleted(result)

        with(hookQueryRepository.get(result.hookId)) {
            assertThat(name, equalTo(HookName("test-hook")))

            flowQueryRepository.get(flowId).let {
                assertThat(it.id, equalTo(flow.id))
                assertThat(it.name, equalTo(FlowName("hamal::flow")))
            }
        }
    }

    @Test
    fun `Tries to create hook with flow which does not exist`() {

        val response = httpTemplate.post("/v1/flows/12345/hooks")
            .path("groupId", testGroup.id)
            .body(ApiHookCreateRequest(HookName("test-hook")))
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is HttpErrorResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Flow not found"))

        assertThat(listHooks().hooks, empty())
    }
}