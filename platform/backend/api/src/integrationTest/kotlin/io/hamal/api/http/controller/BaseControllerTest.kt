package io.hamal.api.http.controller

import io.hamal.api.BaseTest
import io.hamal.lib.domain._enum.RequestStatus.Completed
import io.hamal.lib.domain._enum.RequestStatus.Failed
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.AuthToken.Companion.AuthToken
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiRequested
import io.hamal.repository.api.RequestQueryRepository.RequestQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import kotlin.reflect.KClass

internal abstract class BaseControllerTest : BaseTest() {

    val sdk: ApiSdkImpl by lazy {
        ApiSdkImpl(
            apiHost = "http://localhost:${localPort}",
            token = AuthToken("test-token")
        )
    }

    val httpTemplate: HttpTemplate by lazy {
        sdk.template
    }

    fun verifyReqCompleted(id: RequestId) {
        with(requestQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(requestStatus, equalTo(Completed))
        }
    }

    fun verifyReqFailed(id: RequestId) {
        with(requestQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(requestStatus, equalTo(Failed))
        }
    }


    fun awaitCompleted(id: RequestId) {
        while (true) {
            requestQueryRepository.find(id)?.let {
                if (it.requestStatus == Completed) {
                    return
                }
                if (it.requestStatus == Failed) {
                    throw IllegalStateException("expected $id to complete but failed")
                }
            }
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
                if (it.requestStatus == Failed) {
                    return
                }

                if (it.requestStatus == Completed) {
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
        assertThat(requests, empty())
    }

    fun <SUBMITTED_REQ : Requested> verifyNoRequests(clazz: KClass<SUBMITTED_REQ>) {
        val requests = requestQueryRepository.list(RequestQuery()).filterIsInstance(clazz.java)
        assertThat(requests, empty())
    }

}