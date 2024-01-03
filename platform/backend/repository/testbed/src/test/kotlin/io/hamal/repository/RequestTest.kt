package io.hamal.repository

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.RequestStatus.*
import io.hamal.lib.domain.request.TestRequested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.RequestQueryRepository.ReqQuery
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
            queue(TestRequested(RequestId(1), Submitted))
            verifyCount(1)
        }
    }

    @Nested
    inner class NextTest {
        @TestFactory
        fun `Nothing there`() = runWith(RequestRepository::class) {
            val result = next(10)
            assertThat(result, empty())
        }

        @TestFactory
        fun `Less reqs there than limit`() = runWith(RequestRepository::class) {
            queue(TestRequested(RequestId(1), Submitted))
            queue(TestRequested(RequestId(2), Submitted))
            queue(TestRequested(RequestId(3), Submitted))
            queue(TestRequested(RequestId(4), Submitted))

            val result = next(5)
            assertThat(result, hasSize(4))
            assertThat(result[0].id, equalTo(RequestId(1)))
            assertThat(result[1].id, equalTo(RequestId(2)))
            assertThat(result[2].id, equalTo(RequestId(3)))
            assertThat(result[3].id, equalTo(RequestId(4)))
        }

        @TestFactory
        fun `Limit req amount`() = runWith(RequestRepository::class) {
            queue(TestRequested(RequestId(1), Submitted))
            queue(TestRequested(RequestId(2), Submitted))
            queue(TestRequested(RequestId(3), Submitted))
            queue(TestRequested(RequestId(4), Submitted))

            val result = next(2)
            assertThat(result, hasSize(2))
            assertThat(result[0].id, equalTo(RequestId(1)))
            assertThat(result[1].id, equalTo(RequestId(2)))
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
            createReq(RequestId(1), Submitted)
            createReq(RequestId(2), Completed)

            clear()
            verifyCount(0)
        }
    }

    @Nested
    inner class CompleteTest {

        @TestFactory
        fun `Complete submitted req`() = runWith(RequestRepository::class) {
            createReq(RequestId(234), Submitted)

            complete(RequestId(234))

            with(get(RequestId(234))) {
                assertThat(status, equalTo(Completed))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to complete but Submitted state`() = RequestStatus.values().filter { it != Submitted }
            .flatMap { reqStatus ->
                runWith(RequestRepository::class, reqStatus.name) {
                    createReq(RequestId(234), reqStatus)

                    val exception = assertThrows<IllegalStateException> {
                        complete(RequestId(234))
                    }
                    assertThat(exception.message, equalTo("Req not submitted"))

                    with(get(RequestId(234))) {
                        assertThat(status, equalTo(reqStatus))
                    }
                }
            }

        @TestFactory
        fun `Tries to complete but req does not exist`() = runWith(RequestRepository::class) {
            createReq(RequestId(23), Submitted)

            val exception = assertThrows<NoSuchElementException> {
                complete(RequestId(3223))
            }
            assertThat(exception.message, equalTo("Req not found"))
        }
    }

    @Nested
    inner class FailTest {

        @TestFactory
        fun `Fail submitted req`() = runWith(RequestRepository::class) {
            createReq(RequestId(234), Submitted)

            fail(RequestId(234))

            with(get(RequestId(234))) {
                assertThat(status, equalTo(Failed))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to fail but Submitted state`() = RequestStatus.values().filter { it != Submitted }
            .flatMap { reqStatus ->
                runWith(RequestRepository::class, reqStatus.name) {
                    createReq(RequestId(234), reqStatus)

                    val exception = assertThrows<IllegalStateException> {
                        fail(RequestId(234))
                    }
                    assertThat(exception.message, equalTo("Req not submitted"))

                    with(get(RequestId(234))) {
                        assertThat(status, equalTo(reqStatus))
                    }
                }
            }

        @TestFactory
        fun `Tries to fail but req does not exist`() = runWith(RequestRepository::class) {
            createReq(RequestId(23), Submitted)

            val exception = assertThrows<NoSuchElementException> {
                fail(RequestId(3223))
            }
            assertThat(exception.message, equalTo("Req not found"))
        }
    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get func by id`() = runWith(RequestRepository::class) {
            createReq(RequestId(1), Submitted)
            with(get(RequestId(1))) {
                assertThat(status, equalTo(Submitted))
            }
        }

        @TestFactory
        fun `Tries to get func by id but does not exist`() = runWith(RequestRepository::class) {
            createReq(RequestId(1), Submitted)
            val exception = assertThrows<NoSuchElementException> {
                get(RequestId(111111))
            }
            assertThat(exception.message, equalTo("Req not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find func by id`() = runWith(RequestRepository::class) {
            createReq(RequestId(1), Submitted)
            with(find(RequestId(1))!!) {
                assertThat(status, equalTo(Submitted))
            }
        }

        @TestFactory
        fun `Tries to find func by id but does not exist`() = runWith(RequestRepository::class) {
            createReq(RequestId(1), Submitted)
            val result = find(RequestId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `Limit`() = runWith(RequestRepository::class) {
            setup()
            val query = ReqQuery(limit = Limit(3))

            assertThat(count(query), equalTo(4UL))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(RequestRepository::class) {
            setup()

            val query = ReqQuery(
                afterId = RequestId(3),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(RequestId(4)))
            }
        }

        private fun RequestRepository.setup() {
            createReq(RequestId(1), Submitted)
            createReq(RequestId(2), Completed)
            createReq(RequestId(3), Failed)
            createReq(RequestId(4), Submitted)
        }
    }
}

private fun RequestRepository.createReq(
    reqId: RequestId,
    status: RequestStatus,
) {
    queue(
        TestRequested(
            id = reqId,
            status = status,
        )
    )
}

private fun RequestRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun RequestRepository.verifyCount(expected: Int, block: ReqQuery.() -> Unit) {
    val counted = count(ReqQuery().also(block))
    assertThat("number of reqs expected", counted, equalTo(expected.toULong()))
}