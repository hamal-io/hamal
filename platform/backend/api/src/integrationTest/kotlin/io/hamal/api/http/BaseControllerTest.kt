package io.hamal.api.http

import io.hamal.api.BaseTest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Completed
import io.hamal.lib.domain._enum.ReqStatus.Failed
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.api.ApiSubmittedReq
import io.hamal.repository.api.ReqQueryRepository.ReqQuery
import io.hamal.repository.api.submitted_req.SubmittedReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
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

    fun <REQ : ApiSubmittedReq> awaitFailed(req: REQ): REQ {
        awaitFailed(req.reqId)
        return req
    }

    fun <REQ : ApiSubmittedReq> awaitFailed(reqs: Iterable<REQ>): Iterable<REQ> {
        return reqs.onEach { awaitFailed(it.reqId) }
    }


    fun verifyNoRequests() {
        val requests = reqQueryRepository.list(ReqQuery())
        assertThat(requests, empty())
    }

    fun <SUBMITTED_REQ : SubmittedReq> verifyNoRequests(clazz: KClass<SUBMITTED_REQ>) {
        val requests = reqQueryRepository.list(ReqQuery()).filterIsInstance(clazz.java)
        assertThat(requests, empty())
    }

}