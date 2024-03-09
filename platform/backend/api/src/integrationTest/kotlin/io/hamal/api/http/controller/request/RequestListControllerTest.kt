package io.hamal.api.http.controller.request

import io.hamal.lib.domain._enum.RequestStatus.Completed
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExecCode
import io.hamal.lib.sdk.api.ApiExecInvokeRequested
import io.hamal.lib.sdk.api.ApiRequestList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class RequestListControllerTest : RequestBaseControllerTest() {
    @Test
    fun `No reqs`() {
        with(list()) {
            assertThat(requests, empty())
        }
    }

    @Test
    fun `Single req`() {
        val adhocResponse = awaitCompleted(adhoc())

        with(list()) {
            assertThat(requests, hasSize(1))

            with(requests.first()) {
                assertThat(requestId, equalTo(adhocResponse.requestId))
                assertThat(requestStatus, equalTo(Completed))
                assertThat(this, instanceOf(ApiExecInvokeRequested::class.java))
            }
        }
    }

    @Test
    fun `Limit reqs`() {
        awaitCompleted(IntRange(0, 25).map { adhoc(CodeValue("$it")) })

        val listResponse = httpTemplate.get("/v1/requests")
            .parameter("limit", 23)
            .execute(ApiRequestList::class)

        assertThat(listResponse.requests, hasSize(23))

        listResponse.requests
            .map { it as ApiExecInvokeRequested }
            .forEachIndexed { idx, req ->
                val code = execQueryRepository.get(req.id).code
                assertThat(code, equalTo(ExecCode(value = CodeValue("${22 - idx}"))))
            }
    }

    @Test
    fun `Skip and limit reqs`() {
        val requests = IntRange(0, 100).map { adhoc(CodeValue("$it")) }
        awaitCompleted(requests)

        val request70 = requests[70]

        val listResponse = httpTemplate.get("/v1/requests")
            .parameter("after_id", request70.id)
            .parameter("limit", 1)
            .execute(ApiRequestList::class)

        assertThat(listResponse.requests, hasSize(1))

        listResponse.requests
            .map { it as ApiExecInvokeRequested }
            .forEach { req ->
                val code = execQueryRepository.get(req.id).code
                assertThat(code, equalTo(ExecCode(value = CodeValue("71"))))
            }
    }
}