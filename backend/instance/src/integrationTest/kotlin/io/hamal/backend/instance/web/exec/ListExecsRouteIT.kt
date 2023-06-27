package io.hamal.backend.instance.web.exec

import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.sdk.domain.ListExecsResponse
import io.hamal.lib.sdk.extension.parameter
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ListExecsRouteIT : BaseExecRouteIT() {
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
        val createAdhocResponse = createAdhocExec().also { awaitReqCompleted(it.id) }

        val response = httpTemplate.get("/v1/execs").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse)

        with(response.result(ListExecsResponse::class)) {
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                assertThat(id, equalTo(createAdhocResponse.execId))
                assertThat(status, equalTo(ExecStatus.Queued))
            }
        }
    }

    @Test
    fun `Limit execs`() {
        val requests = IntRange(1, 100).map { createAdhocExec() }
        requests.forEach { awaitReqCompleted(it.id) }

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
        val requests = IntRange(1, 100).map { createAdhocExec() }
        val fortySixthRequest = requests.drop(45).take(1).first()
        val fortySeventhRequest = requests.drop(46).take(1).first()

        requests.forEach { awaitReqCompleted(it.id) }

        val response = httpTemplate.get("/v1/execs")
            .parameter("limit", 1)
            .parameter("after_id", fortySixthRequest.execId)
            .execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse)

        with(response.result(ListExecsResponse::class)) {
            assertThat(execs, hasSize(1))
            execs.forEach { exec ->
                assertThat(exec.id, equalTo(fortySeventhRequest.execId))
                assertThat(exec.status, equalTo(ExecStatus.Queued))
            }
        }
    }
}