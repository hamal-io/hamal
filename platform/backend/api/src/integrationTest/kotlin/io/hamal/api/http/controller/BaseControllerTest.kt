package io.hamal.api.http.controller

import io.hamal.api.BaseTest
import io.hamal.lib.domain._enum.RequestStatus.Completed
import io.hamal.lib.domain._enum.RequestStatus.Failed
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.api.ApiRequested
import io.hamal.repository.api.RequestQueryRepository.ReqQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import kotlin.reflect.KClass

internal abstract class BaseControllerTest : BaseTest() {

    val httpTemplate: HttpTemplateImpl by lazy {
        HttpTemplateImpl(
            baseUrl = "http://localhost:${localPort}",
            headerFactory = {
                set("accept", "application/json")
                set("content-type", "application/json")
                set("authorization", "Bearer test-token")
            }
        )
    }

    fun verifyReqCompleted(id: RequestId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(Completed))
        }
    }

    fun verifyReqFailed(id: RequestId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(Failed))
        }
    }


    fun awaitCompleted(id: RequestId) {
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


    fun <SUBMITTED : ApiRequested> awaitCompleted(submitted: SUBMITTED): SUBMITTED {
        awaitCompleted(submitted.id)
        return submitted
    }

    fun <SUBMITTED : ApiRequested> awaitCompleted(vararg reqs: SUBMITTED): Iterable<SUBMITTED> {
        return reqs.toList().onEach { awaitCompleted(it.id) }
    }

    fun <SUBMITTED : ApiRequested> awaitCompleted(reqs: Iterable<SUBMITTED>): Iterable<SUBMITTED> {
        return reqs.onEach { awaitCompleted(it.id) }
    }

    fun awaitFailed(id: RequestId) {
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

    fun <SUBMITTED : ApiRequested> awaitFailed(req: SUBMITTED): SUBMITTED {
        awaitFailed(req.id)
        return req
    }

    fun <SUBMITTED : ApiRequested> awaitFailed(reqs: Iterable<SUBMITTED>): Iterable<SUBMITTED> {
        return reqs.onEach { awaitFailed(it.id) }
    }

    fun verifyNoRequests() {
        val requests = reqQueryRepository.list(ReqQuery())
        assertThat(requests, empty())
    }

    fun <SUBMITTED_REQ : Requested> verifyNoRequests(clazz: KClass<SUBMITTED_REQ>) {
        val requests = reqQueryRepository.list(ReqQuery()).filterIsInstance(clazz.java)
        assertThat(requests, empty())
    }

}