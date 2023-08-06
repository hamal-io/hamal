package io.hamal.backend.instance.web.state

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.TableValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class GetStateRouteTest : BaseStateRouteTest() {

    @Test
    fun `Get state`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id

        val execId = createExec(
            execId = ExecId(123),
            status = ExecStatus.Started,
            correlation = Correlation(
                funcId = funcId,
                correlationId = CorrelationId("__1__")
            )
        ).id

        awaitCompleted(completeExec(execId, State(TableValue("hamal" to StringValue("rocks")))))

        val response = httpTemplate.get("/v1/funcs/${funcId.value.value}/states/__1__").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse) { "request was not successful" }

        val correlatedState = response.result(CorrelatedState::class)
        assertThat(correlatedState.correlation.funcId, equalTo(funcId))
        assertThat(correlatedState.correlation.correlationId, equalTo(CorrelationId("__1__")))
        assertThat(correlatedState.value, equalTo(State(TableValue("hamal" to StringValue("rocks")))))
    }

    @Test
    fun `Get state for function which was never set before`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id

        val response = httpTemplate.get("/v1/funcs/${funcId.value.value}/states/__1__").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse) { "request was not successful" }

        val correlatedState = response.result(CorrelatedState::class)
        assertThat(correlatedState.correlation.funcId, equalTo(funcId))
        assertThat(correlatedState.correlation.correlationId, equalTo(CorrelationId("__1__")))
        assertThat(correlatedState.value, equalTo(State()))
    }

    @Test
    fun `Tries to get state but func does not exists`() {
        val response = httpTemplate.get("/v1/funcs/123456765432/states/1").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

        val error = response.error(HamalError::class)
        assertThat(error.message, equalTo("Func not found"))
    }
}