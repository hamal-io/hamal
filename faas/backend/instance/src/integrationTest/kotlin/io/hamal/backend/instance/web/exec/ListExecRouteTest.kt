package io.hamal.backend.instance.web.exec

import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.sdk.domain.ListExecsResponse
import io.hamal.lib.sdk.extension.parameter
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ListExecRouteTest : BaseExecRouteTest() {
    @Test
    fun `No execs`() {
        val response = httpTemplate.get("/v1/execs").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse)

        val result = response.result(ListExecsResponse::class)
        assertThat(result.execs, empty())
    }

    @Test
    fun `Single exec`() {
        val createAdhocResponse = awaitCompleted(
            createAdhocExec()
        )

        val response = httpTemplate.get("/v1/execs").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse)

        with(response.result(ListExecsResponse::class)) {
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                assertThat(id, equalTo(createAdhocResponse.id))
                assertThat(status, equalTo(ExecStatus.Queued))
            }
        }
    }

    @Test
    fun `Limit execs`() {
        awaitCompleted(
            IntRange(1, 50).map { createAdhocExec() }
        )

        val response = httpTemplate.get("/v1/execs")
            .parameter("limit", 42)
            .execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse)

        with(response.result(ListExecsResponse::class)) {
            assertThat(execs, hasSize(42))
            execs.forEach { exec ->
                assertThat(exec.status, equalTo(ExecStatus.Queued))
            }
        }
    }

    @Test
    fun `Skip and limit execs`() {
        val requests = awaitCompleted(IntRange(1, 100).map { createAdhocExec() })

        val fortyFifthRequest = requests.drop(44).take(1).first()
        val fortySixthRequest = requests.drop(45).take(1).first()

        val response = httpTemplate.get("/v1/execs")
            .parameter("limit", 1)
            .parameter("after_id", fortySixthRequest.id)
            .execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse)

        with(response.result(ListExecsResponse::class)) {
            assertThat(execs, hasSize(1))
            execs.forEach { exec ->
                assertThat(exec.id, equalTo(fortyFifthRequest.id))
                assertThat(exec.status, equalTo(ExecStatus.Queued))
            }
        }
    }
}