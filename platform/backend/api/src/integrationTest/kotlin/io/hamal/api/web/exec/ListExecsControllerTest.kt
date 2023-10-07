package io.hamal.api.web.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.sdk.api.ApiExecList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ListExecsControllerTest : BaseExecControllerTest() {
    @Test
    fun `No execs`() {
        val response = httpTemplate.get("/v1/groups/{groupId}/execs")
            .path("groupId", testGroup.id)
            .execute()

        assertThat(response.statusCode, equalTo(Ok))
        require(response is SuccessHttpResponse)

        val result = response.result(ApiExecList::class)
        assertThat(result.execs, empty())
    }

    @Test
    fun `Single exec`() {
        val execId = awaitCompleted(createAdhocExec()).id(::ExecId)

        val response = httpTemplate.get("/v1/groups/{groupId}/execs")
            .path("groupId", testGroup.id)
            .execute()

        assertThat(response.statusCode, equalTo(Ok))
        require(response is SuccessHttpResponse)

        with(response.result(ApiExecList::class)) {
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                assertThat(id, equalTo(execId))
            }
        }
    }

    @Test
    fun `Limit execs`() {
        awaitCompleted(
            IntRange(1, 50).map { createAdhocExec() }
        )

        val response = httpTemplate.get("/v1/groups/{groupId}/execs")
            .path("groupId", testGroup.id)
            .parameter("limit", 42)
            .execute()

        assertThat(response.statusCode, equalTo(Ok))
        require(response is SuccessHttpResponse)

        with(response.result(ApiExecList::class)) {
            assertThat(execs, hasSize(42))
        }
    }

    @Test
    fun `Skip and limit execs`() {
        val requests = awaitCompleted(IntRange(1, 100).map { createAdhocExec() })

        val fortyFifthRequest = requests.drop(44).take(1).first()
        val fortySixthRequest = requests.drop(45).take(1).first()

        val response = httpTemplate.get("/v1/groups/{groupId}/execs")
            .path("groupId", testGroup.id)
            .parameter("limit", 1)
            .parameter("after_id", fortySixthRequest.id)
            .execute()

        assertThat(response.statusCode, equalTo(Ok))
        require(response is SuccessHttpResponse)

        with(response.result(ApiExecList::class)) {
            assertThat(execs, hasSize(1))
            execs.forEach { exec ->
                assertThat(exec.id, equalTo(fortyFifthRequest.id(::ExecId)))
            }
        }
    }
}