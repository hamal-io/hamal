package io.hamal.api.web.req

import io.hamal.lib.domain._enum.ReqStatus.Completed
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.api.ApiReqList
import io.hamal.lib.sdk.api.ApiSubmittedReq
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ListReqsControllerTest : BaseReqControllerTest() {
    @Test
    fun `No reqs`() {
        with(list()) {
            assertThat(reqs, empty())
        }
    }

    @Test
    fun `Single req`() {
        val adhocResponse = awaitCompleted(adhoc())

        with(list()) {
            assertThat(reqs, hasSize(1))

            with(reqs.first()) {
                assertThat(reqId, equalTo(adhocResponse.reqId))
                assertThat(status, equalTo(Completed))
                assertThat(this, instanceOf(ApiSubmittedReq::class.java))
            }
        }
    }

    @Test
    fun `Limit reqs`() {
        awaitCompleted(IntRange(0, 25).map { adhoc(CodeValue("$it")) })

        val listResponse = httpTemplate.get("/v1/reqs")
            .parameter("limit", 23)
            .execute(ApiReqList::class)

        assertThat(listResponse.reqs, hasSize(23))

        listResponse.reqs
            .map { it as ApiSubmittedReqWithId }
            .forEachIndexed { idx, req ->
                val code = execQueryRepository.get(req.id(::ExecId)).code
                assertThat(code, equalTo(CodeValue("${22 - idx}")))
            }
    }

    @Test
    fun `Skip and limit reqs`() {
        val requests = IntRange(0, 100).map { adhoc(CodeValue("$it")) }
        awaitCompleted(requests)

        val request70 = requests[70]

        val listResponse = httpTemplate.get("/v1/reqs")
            .parameter("after_id", request70.reqId)
            .parameter("limit", 1)
            .execute(ApiReqList::class)

        assertThat(listResponse.reqs, hasSize(1))

        listResponse.reqs
            .map { it as ApiSubmittedReqWithId }
            .forEach { req ->
                val code = execQueryRepository.get(req.id(::ExecId)).code
                assertThat(code, equalTo(CodeValue("71")))
            }
    }
}