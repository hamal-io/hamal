package io.hamal.bridge.web

import io.hamal.bridge.BaseTest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.hub.HubSubmittedReq
import io.hamal.repository.api.submitted_req.SubmittedReq
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import kotlin.reflect.KClass

internal abstract class BaseRouteTest : BaseTest() {

    val httpTemplate: HttpTemplate by lazy {
        HttpTemplate(
            baseUrl = "http://localhost:${localPort}",
            headerFactory = {
                set("x-hamal-token", "test-token")
            }
        )
    }

    fun verifyReqCompleted(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            org.hamcrest.MatcherAssert.assertThat(id, org.hamcrest.Matchers.equalTo(id))
            org.hamcrest.MatcherAssert.assertThat(
                status,
                org.hamcrest.Matchers.equalTo(io.hamal.lib.domain._enum.ReqStatus.Completed)
            )
        }
    }

    fun verifyReqFailed(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            org.hamcrest.MatcherAssert.assertThat(id, org.hamcrest.Matchers.equalTo(id))
            org.hamcrest.MatcherAssert.assertThat(
                status,
                org.hamcrest.Matchers.equalTo(io.hamal.lib.domain._enum.ReqStatus.Failed)
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

    fun <REQ : HubSubmittedReq> awaitCompleted(req: REQ): REQ {
        awaitCompleted(req.reqId)
        return req
    }

    fun <REQ : HubSubmittedReq> awaitCompleted(vararg reqs: REQ): Iterable<REQ> {
        return reqs.toList().onEach { awaitCompleted(it.reqId) }
    }

    fun <REQ : HubSubmittedReq> awaitCompleted(reqs: Iterable<REQ>): Iterable<REQ> {
        return reqs.onEach { awaitCompleted(it.reqId) }
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

    fun <REQ : HubSubmittedReq> awaitFailed(req: REQ): REQ {
        awaitFailed(req.reqId)
        return req
    }

    fun <REQ : HubSubmittedReq> awaitFailed(reqs: Iterable<REQ>): Iterable<REQ> {
        return reqs.onEach { awaitFailed(it.reqId) }
    }


    fun verifyNoRequests() {
        val requests = reqQueryRepository.list { }
        MatcherAssert.assertThat(requests, Matchers.empty())
    }

    fun <SUBMITTED_REQ : SubmittedReq> verifyNoRequests(clazz: KClass<SUBMITTED_REQ>) {
        val requests = reqQueryRepository.list { }.filterIsInstance(clazz.java)
        MatcherAssert.assertThat(requests, Matchers.empty())
    }

}