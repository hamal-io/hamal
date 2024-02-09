package io.hamal.api.http.controller

import io.hamal.api.BaseTest
import io.hamal.lib.domain._enum.RequestStatus.Completed
import io.hamal.lib.domain._enum.RequestStatus.Failed
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiRequested
import io.hamal.repository.api.RequestQueryRepository.ReqQuery
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
            assertThat(status, equalTo(Completed))
        }
    }

    fun verifyReqFailed(id: RequestId) {
        with(requestQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(Failed))
        }
    }


    fun awaitCompleted(id: RequestId) {
        while (true) {
            requestQueryRepository.find(id)?.let {
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
            requestQueryRepository.find(id)?.let {
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
        val requests = requestQueryRepository.list(ReqQuery())
        assertThat(requests, empty())
    }

    fun <SUBMITTED_REQ : Requested> verifyNoRequests(clazz: KClass<SUBMITTED_REQ>) {
        val requests = requestQueryRepository.list(ReqQuery()).filterIsInstance(clazz.java)
        assertThat(requests, empty())
    }

}