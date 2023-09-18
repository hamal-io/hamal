package io.hamal.admin.web.state

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus.Started
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.admin.AdminCorrelatedState
import io.hamal.lib.sdk.admin.AdminError
import io.hamal.lib.sdk.admin.AdminState
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class GetStateControllerTest : BaseStateControllerTest() {

    @Test
    fun `Get state`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id(::FuncId)

        val execId = createExec(
            execId = ExecId(123),
            status = Started,
            correlation = Correlation(
                funcId = funcId,
                correlationId = CorrelationId("__1__")
            )
        ).id

        awaitCompleted(completeExec(execId, State(MapType(mutableMapOf("hamal" to StringType("rocks"))))))

        val response = httpTemplate.get("/v1/funcs/{funcId}/states/__1__").path("funcId", funcId).execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse) { "request was not successful" }

        val correlatedState = response.result(AdminCorrelatedState::class)
        assertThat(correlatedState.correlation.func.id, equalTo(funcId))
        assertThat(correlatedState.correlation.func.name, equalTo(FuncName("SomeFunc")))
        assertThat(correlatedState.correlation.correlationId, equalTo(CorrelationId("__1__")))
        assertThat(correlatedState.state, equalTo(AdminState(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
    }

    @Test
    fun `Get state for function which was never set before`() {
        val funcId = awaitCompleted(createFunc(FuncName("SomeFunc"))).id(::FuncId)

        val response = httpTemplate.get("/v1/funcs/{funcId}/states/__1__").path("funcId", funcId).execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse) { "request was not successful" }

        val correlatedState = response.result(AdminCorrelatedState::class)
        assertThat(correlatedState.correlation.func.id, equalTo(funcId))
        assertThat(correlatedState.correlation.func.name, equalTo(FuncName("SomeFunc")))
        assertThat(correlatedState.correlation.correlationId, equalTo(CorrelationId("__1__")))
        assertThat(correlatedState.state, equalTo(AdminState()))
    }

    @Test
    fun `Tries to get state but func does not exists`() {
        val response = httpTemplate.get("/v1/funcs/123456765432/states/1").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

        val error = response.error(AdminError::class)
        assertThat(error.message, equalTo("Func not found"))
    }
}