package io.hamal.repository.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils.withEpochMilli
import io.hamal.lib.common.util.TimeUtils.withInstant
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.Chunk
import io.hamal.repository.api.log.ChunkId
import io.hamal.repository.api.log.Segment
import io.hamal.repository.api.log.SegmentRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import java.time.Instant


class SegmentRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class AppendTest {

        @TestFactory
        fun `Bytes, Instant does not have to be unique`() = runWith(SegmentRepository::class) { testInstance ->
            withInstant(Instant.ofEpochMilli(1)) {
                testInstance.append(CmdId(1), "SomeBytes".toByteArray())
                testInstance.append(CmdId(2), "SomeBytes".toByteArray())
            }

            assertThat(testInstance.count(), equalTo(2UL))

            testInstance.read(ChunkId(1), 2).let {
                assertThat(it, hasSize(2))

                val chunk = it.first()
                assertThat(chunk.segmentId, equalTo(Segment.Id(2810)))
                assertThat(chunk.topicId, equalTo(TopicId(1506)))
                assertThat(chunk.bytes, equalTo("SomeBytes".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(1)))
            }
        }


        @TestFactory
        fun `Append single chunk`() = runWith(SegmentRepository::class) { testInstance ->
            withInstant(Instant.ofEpochMilli(2810)) {
                testInstance.append(CmdId(1), "VALUE".toByteArray())
            }

            assertThat(testInstance.count(), equalTo(1UL))

            testInstance.read(ChunkId(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(ChunkId(1)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(2810)))
                assertThat(chunk.topicId, equalTo(TopicId(1506)))
                assertThat(chunk.bytes, equalTo("VALUE".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(2810)))
            }
        }

        @TestFactory
        fun `Append multiple chunks to empty segment`() = runWith(SegmentRepository::class) { testInstance ->
            withEpochMilli(123456) {
                listOf(
                    "VALUE_1".toByteArray(), "VALUE_2".toByteArray(), "VALUE_3".toByteArray()
                ).forEachIndexed { index, value -> testInstance.append(CmdId(index), value) }
            }

            assertThat(testInstance.count(), equalTo(3UL))

            testInstance.read(ChunkId(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(ChunkId(1)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(2810)))
                assertThat(chunk.topicId, equalTo(TopicId(1506)))
                assertThat(chunk.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

            testInstance.read(ChunkId(3)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(ChunkId(3)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(2810)))
                assertThat(chunk.topicId, equalTo(TopicId(1506)))
                assertThat(chunk.bytes, equalTo("VALUE_3".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

        }


        @TestFactory
        fun `Append multiple chunks to segment which already contains chunks`() =
            runWith(SegmentRepository::class) { testInstance ->
                testInstance.createThreeChunks()

                listOf(
                    "Hamal".toByteArray(),
                    "Rockz".toByteArray(),
                ).forEachIndexed { index, value -> testInstance.append(CmdId(index + 4), value) }

                assertThat(testInstance.count(), equalTo(5UL))
            }

        @TestFactory
        fun `A chunk was already created with this cmd id`() =
            runWith(SegmentRepository::class) { testInstance ->
                withEpochMilli(123456) {
                    testInstance.createThreeChunks()
                    testInstance.append(CmdId(2), "OTHER_VALUE".toByteArray())
                }

                testInstance.read(ChunkId(2)).let {
                    assertThat(it, hasSize(1))
                    val chunk = it.first()
                    assertThat(chunk.id, equalTo(ChunkId(2)))
                    assertThat(chunk.segmentId, equalTo(Segment.Id(2810)))
                    assertThat(chunk.topicId, equalTo(TopicId(1506)))
                    assertThat(chunk.bytes, equalTo("VALUE_2".toByteArray()))
                    assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
                }
            }

        private fun SegmentRepository.createThreeChunks() {
            append(CmdId(1), "VALUE_1".toByteArray())
            append(CmdId(2), "VALUE_2".toByteArray())
            append(CmdId(3), "VALUE_3".toByteArray())
        }
    }

    @Nested
    inner class ReadTest {

        @TestFactory
        fun `Tries to read from empty segment`() =
            runWith(SegmentRepository::class) { testInstance ->
                val result = testInstance.read(ChunkId(20))
                assertThat(result, hasSize(0))
            }

        @TestFactory
        fun `Tries to read outside from range`() =
            runWith(SegmentRepository::class) { testInstance ->
                testInstance.createOneHundredChunks()
                val result = testInstance.read(ChunkId(200), 100)
                assertThat(result, hasSize(0))
            }

        @TestFactory
        fun `Tries to read with a limit of 0`() =
            runWith(SegmentRepository::class) { testInstance ->
                testInstance.createOneHundredChunks()
                val result = testInstance.read(ChunkId(23), 0)
                assertThat(result, hasSize(0))
            }

        @TestFactory
        fun `Tries to read with a negative limit`() =
            runWith(SegmentRepository::class) { testInstance ->
                testInstance.createOneHundredChunks()
                val result = testInstance.read(ChunkId(23), -20)
                assertThat(result, hasSize(0))
            }

        @TestFactory
        fun `Read exactly one chunk`() =
            runWith(SegmentRepository::class) { testInstance ->
                testInstance.createOneHundredChunks()
                val result = testInstance.read(ChunkId(69))
                assertThat(result, hasSize(1))
                assertChunk(result.first(), 69)
            }

        @TestFactory
        fun `Reads multiple chunks`() =
            runWith(SegmentRepository::class) { testInstance ->
                testInstance.createOneHundredChunks()
                val result = testInstance.read(ChunkId(25), 36)
                assertThat(result, hasSize(36))

                for (id in 0 until 36) {
                    assertChunk(result[id], 25 + id)
                }
            }

        @TestFactory
        fun `Read is only partially covered by segment`() =
            runWith(SegmentRepository::class) { testInstance ->
                testInstance.createOneHundredChunks()
                val result = testInstance.read(ChunkId(90), 40)
                assertThat(result, hasSize(11))

                for (id in 0 until 11) {
                    assertChunk(result[id], 90 + id)
                }
            }

        private fun assertChunk(chunk: Chunk, id: Int) {
            assertThat(chunk.id, equalTo(ChunkId(id)))
            assertThat(chunk.bytes, equalTo("VALUE_$id".toByteArray()))
            assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(id.toLong())))
        }

        private fun SegmentRepository.createOneHundredChunks() {
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
            runWith(SegmentRepository::class) { testInstance ->
                testInstance.close()
            }

        @TestFactory
        fun `Closing an already closed connection is not a problem`() =
            runWith(SegmentRepository::class) { testInstance ->
                testInstance.close()
                testInstance.close()
                testInstance.close()
                testInstance.close()
                testInstance.close()
            }
    }
}