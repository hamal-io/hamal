package io.hamal.backend.instance.web.exec

import io.hamal.lib.domain.Exec
import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecSecrets
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

internal class GetExecRouteIT : BaseExecRouteIT() {
    @Test
    fun `Get exec`() {
        val createAdhocResponse = createAdhocExec()
        Thread.sleep(10)

        val response = httpTemplate.get("/v1/execs/${createAdhocResponse.execId.value}").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse)

        with(response.result(Exec::class)) {
            assertThat(id, equalTo(createAdhocResponse.execId))
            assertThat(status, equalTo(ExecStatus.Queued))
            assertThat(inputs, equalTo(ExecInputs()))
            assertThat(secrets, equalTo(ExecSecrets()))
            assertThat(code, equalTo(Code("40 + 2")))
            assertThat(correlation, nullValue())
        }
    }

    @Test
    fun `Tries to get exec which does not exist`() {
        val response = httpTemplate.get("/v1/execs/123456765432").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

        val error = response.error(HamalError::class)
        assertThat(error.message, equalTo("Exec not found"))
    }
}