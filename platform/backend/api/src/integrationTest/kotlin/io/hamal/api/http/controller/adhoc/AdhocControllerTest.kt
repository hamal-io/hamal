package io.hamal.api.http.controller.adhoc

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain._enum.CodeTypes.Lua54
import io.hamal.lib.domain._enum.RequestStatuses.Processing
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.vo.CodeType.Companion.CodeType
import io.hamal.lib.domain.vo.ExecCode
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiAdhocInvokeRequest
import io.hamal.lib.sdk.api.ApiExecInvokeRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test


internal class AdhocControllerTest : BaseControllerTest() {

    @Test
    fun `Submits adhoc requests without inputs or secrets`() {
        val response = request(
            ApiAdhocInvokeRequest(
                inputs = InvocationInputs(),
                code = ValueCode("40 + 2"),
                codeType = CodeType(Lua54)
            )
        )

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }

        val result = awaitCompleted(response.result(ApiExecInvokeRequested::class))
        assertThat(result.requestStatus, oneOf(Submitted, Processing))

        verifyReqCompleted(result.requestId)
        verifyExecQueued(result.id)
    }

    private fun request(req: ApiAdhocInvokeRequest) =
        httpTemplate
            .post("/v1/namespaces/{namespaceId}/adhoc")
            .path("namespaceId", testNamespace.id)
            .body(req)
            .execute()


    private fun verifyExecQueued(execId: ExecId) {
        with(execQueryRepository.find(execId)!!) {
            assertThat(id, equalTo(execId))
            assertThat(correlation, nullValue())
            assertThat(inputs, equalTo(ExecInputs()))
            assertThat(code, equalTo(ExecCode(value = ValueCode("40 + 2"), type = CodeType(Lua54))))
        }
    }
}

