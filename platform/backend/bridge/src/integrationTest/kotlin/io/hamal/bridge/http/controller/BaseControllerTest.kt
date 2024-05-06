package io.hamal.bridge.http.controller

import io.hamal.bridge.BaseTest
import io.hamal.lib.domain._enum.RequestStatuses.Completed
import io.hamal.lib.domain._enum.RequestStatuses.Failed
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.api.ApiRequested
import io.hamal.repository.api.RequestQueryRepository.RequestQuery
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

    fun verifyReqCompleted(id: RequestId) {
        with(requestQueryRepository.find(id)!!) {
            MatcherAssert.assertThat(id, Matchers.equalTo(id))
            MatcherAssert.assertThat(
                requestStatus.enumValue,
                Matchers.equalTo(Completed)
            )
        }
    }

    fun verifyReqFailed(id: RequestId) {
        with(requestQueryRepository.find(id)!!) {
            MatcherAssert.assertThat(id, Matchers.equalTo(id))
            MatcherAssert.assertThat(
                requestStatus.enumValue,
                Matchers.equalTo(Failed)
            )
        }
    }


    fun awaitCompleted(id: RequestId) {
        while (true) {
            requestQueryRepository.find(id)?.let {
                if (it.requestStatus.enumValue == Completed) {
                    return
                }
                if (it.requestStatus.enumValue == Failed) {
                    throw IllegalStateException("expected $id to complete but failed")
                }
            }
            Thread.sleep(1)
        }
    }

    fun <SUBMITTED : ApiRequested> awaitCompleted(submitted: SUBMITTED): SUBMITTED {
        awaitCompleted(submitted.requestId)
        return submitted
    }

    fun <SUBMITTED : ApiRequested> awaitCompleted(vararg reqs: SUBMITTED): Iterable<SUBMITTED> {
        return reqs.toList().onEach { awaitCompleted(it.requestId) }
    }

    fun <SUBMITTED : ApiRequested> awaitCompleted(reqs: Iterable<SUBMITTED>): Iterable<SUBMITTED> {
        return reqs.onEach { awaitCompleted(it.requestId) }
    }

    fun awaitFailed(id: RequestId) {
        while (true) {
            requestQueryRepository.find(id)?.let {
                if (it.requestStatus.enumValue == Failed) {
                    return
                }

                if (it.requestStatus.enumValue == Completed) {
                    throw IllegalStateException("expected $id to fail but completed")
                }
            }
            Thread.sleep(1)
        }
    }

    fun <SUBMITTED : ApiRequested> awaitFailed(req: SUBMITTED): SUBMITTED {
        awaitFailed(req.requestId)
        return req
    }

    fun <SUBMITTED : ApiRequested> awaitFailed(reqs: Iterable<SUBMITTED>): Iterable<SUBMITTED> {
        return reqs.onEach { awaitFailed(it.requestId) }
    }


    fun verifyNoRequests() {
        val requests = requestQueryRepository.list(RequestQuery())
        MatcherAssert.assertThat(requests, Matchers.empty())
    }

    fun <SUBMITTED_REQ : Requested> verifyNoRequests(clazz: KClass<SUBMITTED_REQ>) {
        val requests = requestQueryRepository.list(RequestQuery()).filterIsInstance(clazz.java)
        MatcherAssert.assertThat(requests, Matchers.empty())
    }

}