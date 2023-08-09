package io.hamal.backend.instance.web

import io.hamal.backend.instance.BaseTest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Completed
import io.hamal.lib.domain._enum.ReqStatus.Failed
import io.hamal.lib.domain.req.SubmittedReq
import io.hamal.lib.http.HttpTemplate
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import kotlin.reflect.KClass

internal abstract class BaseRouteTest : BaseTest() {

    val httpTemplate: HttpTemplate by lazy {
        HttpTemplate(
            baseUrl = "http://localhost:${localPort}"
        )
    }

    fun verifyReqCompleted(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(Completed))
        }
    }

    fun verifyReqFailed(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(Failed))
        }
    }


    fun awaitCompleted(id: ReqId) {
        while (true) {
            reqQueryRepository.find(id)?.let {
                if (it.status == Completed) {
                    return
                }
                if (it.status == Failed) {
                    throw IllegalStateException("expected $id to complete but failed")
                }
            }
            Thread.sleep(1)
        }
    }

    fun <REQ : SubmittedReq> awaitCompleted(req: REQ): REQ {
        awaitCompleted(req.reqId)
        return req
    }

    fun <REQ : SubmittedReq> awaitCompleted(vararg reqs: REQ): Iterable<REQ> {
        return reqs.toList().onEach { awaitCompleted(it.reqId) }
    }

    fun <REQ : SubmittedReq> awaitCompleted(reqs: Iterable<REQ>): Iterable<REQ> {
        return reqs.onEach { awaitCompleted(it.reqId) }
    }

    fun awaitFailed(id: ReqId) {
        while (true) {
            reqQueryRepository.find(id)?.let {
                if (it.status == Failed) {
                    return
                }

                if (it.status == Completed) {
                    throw IllegalStateException("expected $id to fail but completed")
                }
            }
            Thread.sleep(1)
        }
    }

    fun <REQ : SubmittedReq> awaitFailed(req: REQ): REQ {
        awaitFailed(req.reqId)
        return req
    }

    fun <REQ : SubmittedReq> awaitFailed(reqs: Iterable<REQ>): Iterable<REQ> {
        return reqs.onEach { awaitFailed(it.reqId) }
    }


    fun verifyNoRequests() {
        val requests = reqQueryRepository.list { }
        assertThat(requests, empty())
    }

    fun <SUBMITTED_REQ : SubmittedReq> verifyNoRequests(clazz: KClass<SUBMITTED_REQ>) {
        val requests = reqQueryRepository.list { }.filterIsInstance(clazz.java)
        assertThat(requests, empty())
    }

}