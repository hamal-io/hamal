package io.hamal.repository

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain._enum.ReqStatus.*
import io.hamal.repository.api.ReqQueryRepository.ReqQuery
import io.hamal.repository.api.ReqRepository
import io.hamal.repository.api.submitted_req.TestSubmittedReq
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class ReqRepositoryTest : AbstractUnitTest() {
    @Nested
    inner class QueueTest {
        @TestFactory
        fun `Queues req`() = runWith(ReqRepository::class) {
            queue(TestSubmittedReq(ReqId(1), Submitted))
            verifyCount(1)
        }
    }

    @Nested
    inner class NextTest {
        @TestFactory
        fun `Nothing there`() = runWith(ReqRepository::class) {
            val result = next(10)
            assertThat(result, empty())
        }

        @TestFactory
        fun `Less reqs there than limit`() = runWith(ReqRepository::class) {
            queue(TestSubmittedReq(ReqId(1), Submitted))
            queue(TestSubmittedReq(ReqId(2), Submitted))
            queue(TestSubmittedReq(ReqId(3), Submitted))
            queue(TestSubmittedReq(ReqId(4), Submitted))

            val result = next(5)
            assertThat(result, hasSize(4))
            assertThat(result[0].reqId, equalTo(ReqId(1)))
            assertThat(result[1].reqId, equalTo(ReqId(2)))
            assertThat(result[2].reqId, equalTo(ReqId(3)))
            assertThat(result[3].reqId, equalTo(ReqId(4)))
        }

        @TestFactory
        fun `Limit req amount`() = runWith(ReqRepository::class) {
            queue(TestSubmittedReq(ReqId(1), Submitted))
            queue(TestSubmittedReq(ReqId(2), Submitted))
            queue(TestSubmittedReq(ReqId(3), Submitted))
            queue(TestSubmittedReq(ReqId(4), Submitted))

            val result = next(2)
            assertThat(result, hasSize(2))
            assertThat(result[0].reqId, equalTo(ReqId(1)))
            assertThat(result[1].reqId, equalTo(ReqId(2)))
        }
    }

    @Nested
    inner class ClearTest {
        @TestFactory
        fun `Nothing to clear`() = runWith(ReqRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(ReqRepository::class) {
            createReq(ReqId(1), Submitted)
            createReq(ReqId(2), Completed)

            clear()
            verifyCount(0)
        }
    }

    @Nested
    inner class CompleteTest {

        @TestFactory
        fun `Complete submitted req`() = runWith(ReqRepository::class) {
            createReq(ReqId(234), Submitted)

            complete(ReqId(234))

            with(get(ReqId(234))) {
                assertThat(status, equalTo(Completed))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to complete but Submitted state`() = ReqStatus.values().filter { it != Submitted }
            .flatMap { reqStatus ->
                runWith(ReqRepository::class, reqStatus.name) {
                    createReq(ReqId(234), reqStatus)

                    val exception = assertThrows<IllegalStateException> {
                        complete(ReqId(234))
                    }
                    assertThat(exception.message, equalTo("Req not submitted"))

                    with(get(ReqId(234))) {
                        assertThat(status, equalTo(reqStatus))
                    }
                }
            }

        @TestFactory
        fun `Tries to complete but req does not exist`() = runWith(ReqRepository::class) {
            createReq(ReqId(23), Submitted)

            val exception = assertThrows<NoSuchElementException> {
                complete(ReqId(3223))
            }
            assertThat(exception.message, equalTo("Req not found"))
        }
    }

    @Nested
    inner class FailTest {

        @TestFactory
        fun `Fail submitted req`() = runWith(ReqRepository::class) {
            createReq(ReqId(234), Submitted)

            fail(ReqId(234))

            with(get(ReqId(234))) {
                assertThat(status, equalTo(Failed))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to fail but Submitted state`() = ReqStatus.values().filter { it != Submitted }
            .flatMap { reqStatus ->
                runWith(ReqRepository::class, reqStatus.name) {
                    createReq(ReqId(234), reqStatus)

                    val exception = assertThrows<IllegalStateException> {
                        fail(ReqId(234))
                    }
                    assertThat(exception.message, equalTo("Req not submitted"))

                    with(get(ReqId(234))) {
                        assertThat(status, equalTo(reqStatus))
                    }
                }
            }

        @TestFactory
        fun `Tries to fail but req does not exist`() = runWith(ReqRepository::class) {
            createReq(ReqId(23), Submitted)

            val exception = assertThrows<NoSuchElementException> {
                fail(ReqId(3223))
            }
            assertThat(exception.message, equalTo("Req not found"))
        }
    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get func by id`() = runWith(ReqRepository::class) {
            createReq(ReqId(1), Submitted)
            with(get(ReqId(1))) {
                assertThat(status, equalTo(Submitted))
            }
        }

        @TestFactory
        fun `Tries to get func by id but does not exist`() = runWith(ReqRepository::class) {
            createReq(ReqId(1), Submitted)
            val exception = assertThrows<NoSuchElementException> {
                get(ReqId(111111))
            }
            assertThat(exception.message, equalTo("Req not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find func by id`() = runWith(ReqRepository::class) {
            createReq(ReqId(1), Submitted)
            with(find(ReqId(1))!!) {
                assertThat(status, equalTo(Submitted))
            }
        }

        @TestFactory
        fun `Tries to find func by id but does not exist`() = runWith(ReqRepository::class) {
            createReq(ReqId(1), Submitted)
            val result = find(ReqId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `Limit`() = runWith(ReqRepository::class) {
            setup()
            val query = ReqQuery(limit = Limit(3))

            assertThat(count(query), equalTo(4UL))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(ReqRepository::class) {
            setup()

            val query = ReqQuery(
                afterId = ReqId(3),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(reqId, equalTo(ReqId(4)))
            }
        }

        private fun ReqRepository.setup() {
            createReq(ReqId(1), Submitted)
            createReq(ReqId(2), Completed)
            createReq(ReqId(3), Failed)
            createReq(ReqId(4), Submitted)
        }
    }
}

private fun ReqRepository.createReq(
    reqId: ReqId,
    status: ReqStatus,
) {
    queue(
        TestSubmittedReq(
            reqId = reqId,
            status = status,
        )
    )
}

private fun ReqRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun ReqRepository.verifyCount(expected: Int, block: ReqQuery.() -> Unit) {
    val counted = count(ReqQuery().also(block))
    assertThat("number of reqs expected", counted, equalTo(expected.toULong()))
}