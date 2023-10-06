package io.hamal.repository.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils.withEpochMilli
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.Chunk
import io.hamal.repository.api.log.ChunkId
import io.hamal.repository.api.log.Segment
import io.hamal.repository.api.log.TopicRepository
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
        fun `Append multiple records to empty partition`() = runWith(TopicRepository::class) {
            withEpochMilli(98765) {
                listOf(
                    "VALUE_1".toByteArray(),
                    "VALUE_2".toByteArray(),
                    "VALUE_3".toByteArray()
                ).forEachIndexed { index, value -> append(CmdId(index), value) }
                assertThat(count(), equalTo(3UL))
            }

            read(ChunkId(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(ChunkId(1)))
                assertThat(chunk.topicId, equalTo(TopicId(23)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(98765)))
            }

            read(ChunkId(3)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(ChunkId(3)))
                assertThat(chunk.topicId, equalTo(TopicId(23)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_3".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(98765)))
            }
        }
    }

    @Nested
    inner class ReadTest {

        @TestFactory
        fun `Reads multiple chunks`() = runWith(TopicRepository::class) {
            appendOneHundredChunks()
            val result = read(ChunkId(25), 36)
            assertThat(result, hasSize(36))

            for (id in 0 until 36) {
                assertChunk(result[id], 25 + id)
            }
        }

        private fun assertChunk(chunk: Chunk, id: Int) {
            assertThat(chunk.id, equalTo(ChunkId(id)))
            assertThat(chunk.segmentId, equalTo(Segment.Id(0)))
            assertThat(chunk.topicId, equalTo(TopicId(23)))
            assertThat(chunk.bytes, equalTo("VALUE_$id".toByteArray()))
            assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(id.toLong())))
        }

        private fun TopicRepository.appendOneHundredChunks() {
            LongRange(1, 100).forEach {
                withEpochMilli(it) {
                    append(CmdId(it.toInt()), "VALUE_$it".toByteArray())
                }
            }
        }
    }


}