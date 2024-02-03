package io.hamal.repository.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.util.TimeUtils.withEpochMilli
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.new_log.LogEntry
import io.hamal.repository.api.new_log.LogEntryId
import io.hamal.repository.api.new_log.LogSegmentId
import io.hamal.repository.api.new_log.LogTopicRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import java.time.Instant


internal class TopicRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class AppendTest {
        @TestFactory
        fun `Append multiple records to empty partition`() = runWith(LogTopicRepository::class) {
            withEpochMilli(98765) {
                listOf(
                    "VALUE_1".toByteArray(),
                    "VALUE_2".toByteArray(),
                    "VALUE_3".toByteArray()
                ).forEachIndexed { index, value -> append(CmdId(index), value) }
                assertThat(countEntries(), equalTo(3UL))
            }

            read(LogEntryId(1)).also {
                assertThat(it, hasSize(1))
                val entry = it.first()
                assertThat(entry.id, equalTo(LogEntryId(1)))
                assertThat(entry.topicId, equalTo(LogTopicId(23)))
                assertThat(entry.segmentId, equalTo(LogSegmentId(0)))
                assertThat(entry.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(entry.instant, equalTo(Instant.ofEpochMilli(98765)))
            }

            read(LogEntryId(3)).also {
                assertThat(it, hasSize(1))
                val entry = it.first()
                assertThat(entry.id, equalTo(LogEntryId(3)))
                assertThat(entry.topicId, equalTo(LogTopicId(23)))
                assertThat(entry.segmentId, equalTo(LogSegmentId(0)))
                assertThat(entry.bytes, equalTo("VALUE_3".toByteArray()))
                assertThat(entry.instant, equalTo(Instant.ofEpochMilli(98765)))
            }
        }
    }

    @Nested
    inner class ReadTest {

        @TestFactory
        fun `Reads multiple entries`() = runWith(LogTopicRepository::class) {
            appendOneHundredEntries()
            val result = read(LogEntryId(25), Limit(36))
            assertThat(result, hasSize(36))

            for (id in 0 until 36) {
                assertEntry(result[id], 25 + id)
            }
        }

        private fun assertEntry(entry: LogEntry, id: Int) {
            assertThat(entry.id, equalTo(LogEntryId(id)))
            assertThat(entry.segmentId, equalTo(LogSegmentId(0)))
            assertThat(entry.topicId, equalTo(LogTopicId(23)))
            assertThat(entry.bytes, equalTo("VALUE_$id".toByteArray()))
            assertThat(entry.instant, equalTo(Instant.ofEpochMilli(id.toLong())))
        }

        private fun LogTopicRepository.appendOneHundredEntries() {
            LongRange(1, 100).forEach {
                withEpochMilli(it) {
                    append(CmdId(it.toInt()), "VALUE_$it".toByteArray())
                }
            }
        }
    }
}