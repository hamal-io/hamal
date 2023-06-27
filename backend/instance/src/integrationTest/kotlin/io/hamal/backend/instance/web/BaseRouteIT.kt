package io.hamal.backend.instance.web

import io.hamal.backend.instance.BaseIT
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedReq
import io.hamal.lib.http.HttpTemplate
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo

internal abstract class BaseRouteIT : BaseIT() {

    val httpTemplate: HttpTemplate by lazy {
        HttpTemplate(
            baseUrl = "http://localhost:${localPort}"
        )
    }

    fun verifyReqCompleted(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(ReqStatus.Completed))
        }
    }

    fun verifyReqFailed(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(ReqStatus.Failed))
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

    fun <REQ : SubmittedReq> awaitCompleted(req: REQ): REQ {
        return req.also { awaitCompleted(it.id) }
    }

    fun <REQ : SubmittedReq> awaitCompleted(vararg reqs: REQ): Iterable<REQ> {
        return reqs.toList().onEach { awaitCompleted(it.id) }
    }

    fun <REQ : SubmittedReq> awaitCompleted(reqs: Iterable<REQ>): Iterable<REQ> {
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

    fun <REQ : SubmittedReq> awaitFailed(req: REQ): REQ {
        return req.also { awaitFailed(it.id) }
    }

    fun <REQ : SubmittedReq> awaitFailed(reqs: Iterable<REQ>): Iterable<REQ> {
        return reqs.onEach { awaitFailed(it.id) }
    }


    fun verifyNoRequests() {
        val requests = reqQueryRepository.list { }
        assertThat(requests, empty())
    }


}