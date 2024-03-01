package io.hamal.repository.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.util.TimeUtils.withEpochMilli
import io.hamal.lib.common.util.TimeUtils.withInstant
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.log.LogEvent
import io.hamal.repository.api.log.LogEventId
import io.hamal.repository.api.log.LogSegmentId
import io.hamal.repository.api.log.LogSegmentRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import java.time.Instant


internal class LogSegmentRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class AppendTest {

        @TestFactory
        fun `Bytes, Instant does not have to be unique`() = runWith(LogSegmentRepository::class) {
            withInstant(Instant.ofEpochMilli(1)) {
                append(CmdId(1), "SomeBytes".toByteArray())
                append(CmdId(2), "SomeBytes".toByteArray())
            }

            assertThat(count(), equalTo(Count(2)))

            read(LogEventId(1), Limit(2)).let {
                assertThat(it, hasSize(2))

                val entry = it.first()
                assertThat(entry.segmentId, equalTo(LogSegmentId(2810)))
                assertThat(entry.topicId, equalTo(LogTopicId(1506)))
                assertThat(entry.bytes, equalTo("SomeBytes".toByteArray()))
                assertThat(entry.instant, equalTo(Instant.ofEpochMilli(1)))
            }
        }


        @TestFactory
        fun `Append single event`() = runWith(LogSegmentRepository::class) {
            withInstant(Instant.ofEpochMilli(2810)) {
                append(CmdId(1), "VALUE".toByteArray())
            }

            assertThat(count(), equalTo(Count(1)))

            read(LogEventId(1)).let {
                assertThat(it, hasSize(1))

                val entry = it.first()
                assertThat(entry.id, equalTo(LogEventId(1)))
                assertThat(entry.segmentId, equalTo(LogSegmentId(2810)))
                assertThat(entry.topicId, equalTo(LogTopicId(1506)))
                assertThat(entry.bytes, equalTo("VALUE".toByteArray()))
                assertThat(entry.instant, equalTo(Instant.ofEpochMilli(2810)))
            }
        }

        @TestFactory
        fun `Append multiple entrys to empty segment`() = runWith(LogSegmentRepository::class) {
            withEpochMilli(123456) {
                listOf(
                    "VALUE_1".toByteArray(), "VALUE_2".toByteArray(), "VALUE_3".toByteArray()
                ).forEachIndexed { index, value -> append(CmdId(index), value) }
            }

            assertThat(count(), equalTo(Count(3)))

            read(LogEventId(1)).let {
                assertThat(it, hasSize(1))
                val entry = it.first()
                assertThat(entry.id, equalTo(LogEventId(1)))
                assertThat(entry.segmentId, equalTo(LogSegmentId(2810)))
                assertThat(entry.topicId, equalTo(LogTopicId(1506)))
                assertThat(entry.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(entry.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

            read(LogEventId(3)).let {
                assertThat(it, hasSize(1))
                val entry = it.first()
                assertThat(entry.id, equalTo(LogEventId(3)))
                assertThat(entry.segmentId, equalTo(LogSegmentId(2810)))
                assertThat(entry.topicId, equalTo(LogTopicId(1506)))
                assertThat(entry.bytes, equalTo("VALUE_3".toByteArray()))
                assertThat(entry.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

        }


        @TestFactory
        fun `Append multiple events to segment which already contains events`() =
            runWith(LogSegmentRepository::class) {
                createThreeEvents()

                listOf(
                    "Hamal".toByteArray(),
                    "rocks".toByteArray(),
                ).forEachIndexed { index, value -> append(CmdId(index + 4), value) }

                assertThat(count(), equalTo(Count(5)))
            }

        @TestFactory
        fun `A entry was already created with this cmd id`() =
            runWith(LogSegmentRepository::class) {
                withEpochMilli(123456) {
                    createThreeEvents()
                    append(CmdId(2), "OTHER_VALUE".toByteArray())
                }

                read(LogEventId(2)).let {
                    assertThat(it, hasSize(1))
                    val entry = it.first()
                    assertThat(entry.id, equalTo(LogEventId(2)))
                    assertThat(entry.segmentId, equalTo(LogSegmentId(2810)))
                    assertThat(entry.topicId, equalTo(LogTopicId(1506)))
                    assertThat(entry.bytes, equalTo("VALUE_2".toByteArray()))
                    assertThat(entry.instant, equalTo(Instant.ofEpochMilli(123456)))
                }
            }

        private fun LogSegmentRepository.createThreeEvents() {
            append(CmdId(1), "VALUE_1".toByteArray())
            append(CmdId(2), "VALUE_2".toByteArray())
            append(CmdId(3), "VALUE_3".toByteArray())
        }
    }

    @Nested
    inner class ReadTest {

        @TestFactory
        fun `Tries to get from empty segment`() =
            runWith(LogSegmentRepository::class) {
                val result = read(LogEventId(20))
                assertThat(result, hasSize(0))
            }

        @TestFactory
        fun `Tries to get outside from range`() =
            runWith(LogSegmentRepository::class) {
                createOneHundredEvents()
                val result = read(LogEventId(200), Limit(100))
                assertThat(result, hasSize(0))
            }

        @TestFactory
        fun `Read exactly one entry`() =
            runWith(LogSegmentRepository::class) {
                createOneHundredEvents()
                val result = read(LogEventId(69))
                assertThat(result, hasSize(1))
                assertEntry(result.first(), 69)
            }

        @TestFactory
        fun `Reads multiple events`() =
            runWith(LogSegmentRepository::class) {
                createOneHundredEvents()
                val result = read(LogEventId(25), Limit(36))
                assertThat(result, hasSize(36))

                for (id in 0 until 36) {
                    assertEntry(result[id], 25 + id)
                }
            }

        @TestFactory
        fun `Read is only partially covered by segment`() =
            runWith(LogSegmentRepository::class) {
                createOneHundredEvents()
                val result = read(LogEventId(90), Limit(40))
                assertThat(result, hasSize(11))

                for (id in 0 until 11) {
                    assertEntry(result[id], 90 + id)
                }
            }

        private fun assertEntry(entry: LogEvent, id: Int) {
            assertThat(entry.id, equalTo(LogEventId(id)))
            assertThat(entry.bytes, equalTo("VALUE_$id".toByteArray()))
            assertThat(entry.instant, equalTo(Instant.ofEpochMilli(id.toLong())))
        }

        private fun LogSegmentRepository.createOneHundredEvents() {
            IntRange(1, 100).forEach {
                withEpochMilli(it.toLong()) {
                    append(CmdId(it), "VALUE_$it".toByteArray())
                }
            }
        }
    }

    @Nested
    inner class CloseTest {

        @TestFactory
        fun `Close an open repository`() =
            runWith(LogSegmentRepository::class) {
                close()
            }

        @TestFactory
        fun `Closing an already closed connection is not a problem`() =
            runWith(LogSegmentRepository::class) {
                close()
                close()
                close()
                close()
                close()
            }
    }
}