package io.hamal.bridge.http.controller

import io.hamal.bridge.BaseTest
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.ReqQueryRepository.ReqQuery
import io.hamal.repository.api.submitted_req.Submitted
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import kotlin.reflect.KClass

internal abstract class BaseControllerTest : BaseTest() {

    val httpTemplate: HttpTemplateImpl by lazy {
        HttpTemplateImpl(
            baseUrl = "http://localhost:${localPort}",
            headerFactory = {
                set("authorization", "test-token")
            }
        )
    }

    fun verifyReqCompleted(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            MatcherAssert.assertThat(id, Matchers.equalTo(id))
            MatcherAssert.assertThat(
                status,
                Matchers.equalTo(ReqStatus.Completed)
            )
        }
    }

    fun verifyReqFailed(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            MatcherAssert.assertThat(id, Matchers.equalTo(id))
            MatcherAssert.assertThat(
                status,
                Matchers.equalTo(ReqStatus.Failed)
            )
        }
    }


    fun awaitCompleted(id: ReqId) {
        while (true) {
            reqQueryRepository.find(id)?.let {
                if (it.status == ReqStatus.Completed) {
                    return
                }
                if (it.status == ReqStatus.Failed) {
                    throw IllegalStateException("expected $id to complete but failed")
                }
            }
            Thread.sleep(1)
        }
    }

    fun <SUBMITTED : ApiSubmitted> awaitCompleted(submitted: SUBMITTED): SUBMITTED {
        awaitCompleted(submitted.id)
        return submitted
    }

    fun <SUBMITTED : ApiSubmitted> awaitCompleted(vararg reqs: SUBMITTED): Iterable<SUBMITTED> {
        return reqs.toList().onEach { awaitCompleted(it.id) }
    }

    fun <SUBMITTED : ApiSubmitted> awaitCompleted(reqs: Iterable<SUBMITTED>): Iterable<SUBMITTED> {
        return reqs.onEach { awaitCompleted(it.id) }
    }

    fun awaitFailed(id: ReqId) {
        while (true) {
            reqQueryRepository.find(id)?.let {
                if (it.status == ReqStatus.Failed) {
                    return
                }

                if (it.status == ReqStatus.Completed) {
                    throw IllegalStateException("expected $id to fail but completed")
                }
            }
            Thread.sleep(1)
        }
    }

    fun <SUBMITTED : ApiSubmitted> awaitFailed(req: SUBMITTED): SUBMITTED {
        awaitFailed(req.id)
        return req
    }

    fun <SUBMITTED : ApiSubmitted> awaitFailed(reqs: Iterable<SUBMITTED>): Iterable<SUBMITTED> {
        return reqs.onEach { awaitFailed(it.id) }
    }


    fun verifyNoRequests() {
        val requests = reqQueryRepository.list(ReqQuery())
        MatcherAssert.assertThat(requests, Matchers.empty())
    }

    fun <SUBMITTED_REQ : Submitted> verifyNoRequests(clazz: KClass<SUBMITTED_REQ>) {
        val requests = reqQueryRepository.list(ReqQuery()).filterIsInstance(clazz.java)
        MatcherAssert.assertThat(requests, Matchers.empty())
    }

}