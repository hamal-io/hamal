package io.hamal.repository.memory.log

import io.hamal.repository.api.log.LogChunk
import io.hamal.repository.api.log.LogChunkId
import io.hamal.repository.api.log.LogSegment
import io.hamal.lib.common.util.TimeUtils.withEpochMilli
import io.hamal.lib.common.util.TimeUtils.withInstant
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant


class MemoryLogSegmentRepositoryTest {
    @Nested
    inner class AppendTest {
        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Bytes, Instant does not have to be unique`() {
            withInstant(Instant.ofEpochMilli(1)) {
                testInstance.append(CmdId(1), "SomeBytes".toByteArray())
                testInstance.append(CmdId(2), "SomeBytes".toByteArray())
            }

            assertThat(testInstance.count(), equalTo(2UL))

            testInstance.read(LogChunkId(1), 2).let {
                assertThat(it, hasSize(2))

                val chunk = it.first()
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(2810)))
                assertThat(chunk.topicId, equalTo(TopicId(1506)))
                assertThat(chunk.bytes, equalTo("SomeBytes".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(1)))
            }
        }

        @Test
        fun `Append single chunk`() {
            withInstant(Instant.ofEpochMilli(2810)) {
                testInstance.append(CmdId(1), "VALUE".toByteArray())
            }

            assertThat(testInstance.count(), equalTo(1UL))

            testInstance.read(LogChunkId(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(LogChunkId(1)))
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(2810)))
                assertThat(chunk.topicId, equalTo(TopicId(1506)))
                assertThat(chunk.bytes, equalTo("VALUE".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(2810)))
            }
        }

        @Test
        fun `Append multiple chunks to empty segment`() {
            withEpochMilli(123456) {
                listOf(
                    "VALUE_1".toByteArray(),
                    "VALUE_2".toByteArray(),
                    "VALUE_3".toByteArray()
                ).forEachIndexed { index, value -> testInstance.append(CmdId(index), value) }
            }

            assertThat(testInstance.count(), equalTo(3UL))

            testInstance.read(LogChunkId(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(LogChunkId(1)))
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(2810)))
                assertThat(chunk.topicId, equalTo(TopicId(1506)))
                assertThat(chunk.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

            testInstance.read(LogChunkId(3)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(LogChunkId(3)))
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(2810)))
                assertThat(chunk.topicId, equalTo(TopicId(1506)))
                assertThat(chunk.bytes, equalTo("VALUE_3".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

        }

        @Test
        fun `Append multiple chunks to segment which already contains chunks`() {
            givenThreeChunks()

            listOf(
                "Hamal".toByteArray(),
                "Rockz".toByteArray(),
            ).forEachIndexed { index, value -> testInstance.append(CmdId(index + 4), value) }

            assertThat(testInstance.count(), equalTo(5UL))
        }

        @Test
        fun `A chunk was already created with this req id`() {
            givenThreeChunks()

            testInstance.append(CmdId(2), "OTHER_VALUE".toByteArray())
        }


        private fun givenThreeChunks() {
            testInstance.append(CmdId(1), "VALUE_1".toByteArray())
            testInstance.append(CmdId(2), "VALUE_2".toByteArray())
            testInstance.append(CmdId(3), "VALUE_3".toByteArray())
        }

        private val testInstance = MemoryLogSegmentRepository(
            MemoryLogSegment(
                id = LogSegment.Id(2810),
                topicId = TopicId(1506)
            )
        )
    }

    @Nested
    inner class ReadTest {
        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Tries to read from empty segment`() {
            val result = testInstance.read(LogChunkId(20))
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Tries to read outside of segment range`() {
            givenOneHundredChunks()

            val result = testInstance.read(LogChunkId(200), 100)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Tries to read with a limit of 0`() {
            givenOneHundredChunks()

            val result = testInstance.read(LogChunkId(23), 0)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Tries to read with a negative limit`() {
            givenOneHundredChunks()

            val result = testInstance.read(LogChunkId(23), -20)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Read exactly one chunk`() {
            givenOneHundredChunks()

            val result = testInstance.read(LogChunkId(69))
            assertThat(result, hasSize(1))
            assertChunk(result.first(), 69)
        }

        @Test
        fun `Reads multiple chunks`() {
            givenOneHundredChunks()
            val result = testInstance.read(LogChunkId(25), 36)
            assertThat(result, hasSize(36))

            for (id in 0 until 36) {
                assertChunk(result[id], 25 + id)
            }
        }

        @Test
        fun `Read is only partially covered by segment`() {
            givenOneHundredChunks()

            val result = testInstance.read(LogChunkId(90), 40)
            assertThat(result, hasSize(11))

            for (id in 0 until 11) {
                assertChunk(result[id], 90 + id)
            }
        }

        private fun assertChunk(chunk: LogChunk, id: Int) {
            assertThat(chunk.id, equalTo(LogChunkId(id)))
            assertThat(chunk.bytes, equalTo("VALUE_$id".toByteArray()))
            assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(id.toLong())))
        }

        private fun givenOneHundredChunks() {
            IntRange(1, 100).forEach {
                withEpochMilli(it.toLong()) {
                    testInstance.append(CmdId(it), "VALUE_$it".toByteArray())
                }
            }
        }

        private val testInstance = MemoryLogSegmentRepository(
            MemoryLogSegment(
                id = LogSegment.Id(1028),
                topicId = TopicId(1506)
            )
        )

    }

    @Nested
    inner class CloseTest {
        @Test
        fun `Close an open repository`() {
            val testInstance = MemoryLogSegmentRepository(
                MemoryLogSegment(
                    id = LogSegment.Id(1028),
                    topicId = TopicId(1506)
                )
            )

            testInstance.close()
        }

        @Test
        fun `Closing an already closed connection is not a problem`() {
            val testInstance = MemoryLogSegmentRepository(
                MemoryLogSegment(
                    id = LogSegment.Id(1028),
                    topicId = TopicId(1506)
                )
            )

            testInstance.close()
            testInstance.close()
            testInstance.close()
        }
    }
}