package io.hamal.bridge.http

import io.hamal.bridge.BaseTest
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.api.ApiSubmittedReq
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

    fun <REQ : ApiSubmittedReq> awaitCompleted(req: REQ): REQ {
        awaitCompleted(req.reqId)
        return req
    }

    fun <REQ : ApiSubmittedReq> awaitCompleted(vararg reqs: REQ): Iterable<REQ> {
        return reqs.toList().onEach { awaitCompleted(it.reqId) }
    }

    fun <REQ : ApiSubmittedReq> awaitCompleted(reqs: Iterable<REQ>): Iterable<REQ> {
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

    fun <REQ : ApiSubmittedReq> awaitFailed(req: REQ): REQ {
        awaitFailed(req.reqId)
        return req
    }

    fun <REQ : ApiSubmittedReq> awaitFailed(reqs: Iterable<REQ>): Iterable<REQ> {
        return reqs.onEach { awaitFailed(it.reqId) }
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