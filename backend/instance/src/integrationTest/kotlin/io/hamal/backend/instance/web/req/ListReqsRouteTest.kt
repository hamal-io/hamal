package io.hamal.backend.instance.web.req

import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.script.api.value.CodeValue
import io.hamal.lib.sdk.domain.ListSubmittedReqsResponse
import io.hamal.lib.sdk.extension.parameter
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ListReqsRouteTest : BaseReqRouteTest() {
    @Test
    fun `No reqs`() {
        with(list()) {
            assertThat(reqs, empty())
        }
    }

    @Test
    fun `Single req`() {
        val adhocResponse = awaitCompleted(
            adhoc()
        )

        with(list()) {
            assertThat(reqs, hasSize(1))

            with(reqs.first()) {
                assertThat(id, equalTo(adhocResponse.id))
                assertThat(status, equalTo(ReqStatus.Completed))
                assertThat(this, instanceOf(SubmittedInvokeAdhocReq::class.java))
            }
        }
    }

    @Test
    fun `Limit reqs`() {
        awaitCompleted(IntRange(0, 25).map { adhoc(CodeValue("$it")) })

        val listResponse = httpTemplate.get("/v1/reqs")
            .parameter("limit", 23)
            .execute(ListSubmittedReqsResponse::class)

        assertThat(listResponse.reqs, hasSize(23))

        listResponse.reqs
            .map { it as SubmittedInvokeAdhocReq }
            .forEachIndexed { idx, req ->
                val code = execQueryRepository.get(req.execId).code
                assertThat(code, equalTo(CodeValue("${22 - idx}")))
            }
    }

    @Test
    fun `Skip and limit reqs`() {
        val requests = IntRange(0, 100).map { adhoc(CodeValue("$it")) }
        awaitCompleted(requests)

        val request70 = requests[70]

        val listResponse = httpTemplate.get("/v1/reqs")
            .parameter("after_id", request70.id)
            .parameter("limit", 1)
            .execute(ListSubmittedReqsResponse::class)

        assertThat(listResponse.reqs, hasSize(1))

        listResponse.reqs
            .map { it as SubmittedInvokeAdhocReq }
            .forEach { req ->
                val code = execQueryRepository.get(req.execId).code
                assertThat(code, equalTo(CodeValue("71")))
            }
    }
}