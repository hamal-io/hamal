package io.hamal.repository

import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.domain._enum.RequestStatuses
import io.hamal.lib.domain._enum.RequestStatuses.*
import io.hamal.lib.domain.request.TestRequested
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestId.Companion.RequestId
import io.hamal.lib.domain.vo.RequestStatus
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import io.hamal.repository.api.RequestQueryRepository.RequestQuery
import io.hamal.repository.api.RequestRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class RequestRepositoryTest : AbstractUnitTest() {
    @Nested
    inner class QueueTest {
        @TestFactory
        fun `Queues req`() = runWith(RequestRepository::class) {
            queue(TestRequested(RequestId(1), AuthId(1), RequestStatus(Submitted)))
            verifyCount(1)
        }
    }

    @Nested
    inner class NextTest {
        @TestFactory
        fun `Nothing there`() = runWith(RequestRepository::class) {
            val result = next(Limit(10))
            assertThat(result, empty())
        }

        @TestFactory
        fun `Less reqs there than limit`() = runWith(RequestRepository::class) {
            queue(TestRequested(RequestId(1), AuthId(5), RequestStatus(Submitted)))
            queue(TestRequested(RequestId(2), AuthId(6), RequestStatus(Submitted)))
            queue(TestRequested(RequestId(3), AuthId(7), RequestStatus(Submitted)))
            queue(TestRequested(RequestId(4), AuthId(8), RequestStatus(Submitted)))

            val result = next(Limit(5))
            assertThat(result, hasSize(4))
            assertThat(result[0].requestId, equalTo(RequestId(1)))
            assertThat(result[1].requestId, equalTo(RequestId(2)))
            assertThat(result[2].requestId, equalTo(RequestId(3)))
            assertThat(result[3].requestId, equalTo(RequestId(4)))
        }

        @TestFactory
        fun `Limit req amount`() = runWith(RequestRepository::class) {
            queue(TestRequested(RequestId(1), AuthId(5), RequestStatus(Submitted)))
            queue(TestRequested(RequestId(2), AuthId(6), RequestStatus(Submitted)))
            queue(TestRequested(RequestId(3), AuthId(7), RequestStatus(Submitted)))
            queue(TestRequested(RequestId(4), AuthId(8), RequestStatus(Submitted)))

            val result = next(Limit(2))
            assertThat(result, hasSize(2))
            assertThat(result[0].requestId, equalTo(RequestId(1)))
            assertThat(result[1].requestId, equalTo(RequestId(2)))
        }
    }

    @Nested
    inner class ClearTest {
        @TestFactory
        fun `Nothing to clear`() = runWith(RequestRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(RequestRepository::class) {
            createRequest(RequestId(1), RequestStatus(Submitted))
            createRequest(RequestId(2), RequestStatus(Submitted))

            clear()
            verifyCount(0)
        }
    }

    @Nested
    inner class CompleteTest {

        @TestFactory
        fun `Complete processing req`() = runWith(RequestRepository::class) {
            createRequest(RequestId(234), RequestStatus(Processing))

            complete(RequestId(234))

            with(get(RequestId(234))) {
                assertThat(requestStatus, equalTo(Completed))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to complete but not processing`() = RequestStatuses.entries.filter { it != Processing }
            .flatMap { reqStatus ->
                runWith(RequestRepository::class, reqStatus.name) {
                    createRequest(RequestId(234), RequestStatus(reqStatus))

                    val exception = assertThrows<IllegalStateException> {
                        complete(RequestId(234))
                    }
                    assertThat(exception.message, equalTo("Request not processing"))

                    with(get(RequestId(234))) {
                        assertThat(requestStatus, equalTo(reqStatus))
                    }
                }
            }

        @TestFactory
        fun `Tries to complete but request does not exist`() = runWith(RequestRepository::class) {
            createRequest(RequestId(23), RequestStatus(Submitted))

            val exception = assertThrows<NoSuchElementException> {
                complete(RequestId(3223))
            }
            assertThat(exception.message, equalTo("Request not found"))
        }
    }

    @Nested
    inner class FailTest {

        @TestFactory
        fun `Fail processing request`() = runWith(RequestRepository::class) {
            createRequest(RequestId(234), RequestStatus(Processing))

            fail(RequestId(234))

            with(get(RequestId(234))) {
                assertThat(requestStatus, equalTo(Failed))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to fail request but not processing`() = RequestStatuses.entries.filter { it != Processing }
            .flatMap { reqStatus ->
                runWith(RequestRepository::class, reqStatus.name) {
                    createRequest(RequestId(234), RequestStatus(reqStatus))

                    val exception = assertThrows<IllegalStateException> {
                        fail(RequestId(234))
                    }
                    assertThat(exception.message, equalTo("Request not processing"))

                    with(get(RequestId(234))) {
                        assertThat(requestStatus, equalTo(reqStatus))
                    }
                }
            }

        @TestFactory
        fun `Tries to fail but request does not exist`() = runWith(RequestRepository::class) {
            createRequest(RequestId(23), RequestStatus(Submitted))

            val exception = assertThrows<NoSuchElementException> {
                fail(RequestId(3223))
            }
            assertThat(exception.message, equalTo("Request not found"))
        }
    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get request by id`() = runWith(RequestRepository::class) {
            createRequest(RequestId(1), RequestStatus(Submitted))
            with(get(RequestId(1))) {
                assertThat(requestStatus, equalTo(Submitted))
            }
        }

        @TestFactory
        fun `Tries to get request by id but does not exist`() = runWith(RequestRepository::class) {
            createRequest(RequestId(1), RequestStatus(Submitted))
            val exception = assertThrows<NoSuchElementException> {
                get(RequestId(111111))
            }
            assertThat(exception.message, equalTo("Request not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find request by id`() = runWith(RequestRepository::class) {
            createRequest(RequestId(1), RequestStatus(Submitted))
            with(find(RequestId(1))!!) {
                assertThat(requestStatus, equalTo(Submitted))
            }
        }

        @TestFactory
        fun `Tries to request func by id but does not exist`() = runWith(RequestRepository::class) {
            createRequest(RequestId(1), RequestStatus(Submitted))
            val result = find(RequestId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `Limit`() = runWith(RequestRepository::class) {
            setup()
            val query = RequestQuery(limit = Limit(3))

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(RequestRepository::class) {
            setup()

            val query = RequestQuery(
                afterId = RequestId(3),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(requestId, equalTo(RequestId(4)))
            }
        }

        private fun RequestRepository.setup() {
            createRequest(RequestId(1), RequestStatus(Submitted))
            createRequest(RequestId(2), RequestStatus(Completed))
            createRequest(RequestId(3), RequestStatus(Failed))
            createRequest(RequestId(4), RequestStatus(Submitted))
        }
    }
}

private fun RequestRepository.createRequest(
    reqId: RequestId,
    status: RequestStatus,
) {
    queue(
        TestRequested(
            requestId = reqId,
            requestedBy = AuthId(42),
            requestStatus = status,
        )
    )
}

private fun RequestRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun RequestRepository.verifyCount(expected: Int, block: RequestQuery.() -> Unit) {
    val counted = count(RequestQuery().also(block))
    assertThat("number of reqs expected", counted, equalTo(Count(expected)))
}