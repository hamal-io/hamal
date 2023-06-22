package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.LogBroker
import io.hamal.backend.repository.api.log.LogChunk
import io.hamal.backend.repository.api.log.LogChunkId
import io.hamal.backend.repository.api.log.LogSegment
import io.hamal.lib.common.Shard
import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.common.util.TimeUtils.withEpochMilli
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.pathString


class SqliteLogTopicRepositoryTest {
    @Nested
    inner class ConstructorTest {
        @BeforeEach
        fun setup() {
            FileUtils.delete(Path(testDir))
            FileUtils.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet and populates with a shard`() {
            val targetDir = Path(testDir, "another-path", "more-nesting")

            SqliteLogTopicRepository(
                SqliteLogTopic(
                    id = TopicId(23),
                    LogBroker.Id(42),
                    name = TopicName("test-topic"),
                    path = targetDir,
                    shard = Shard(123)
                )
            ).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "topic-00000023")))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "topic-00000023", "shard-0123")))
        }
    }

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
        fun `Append multiple records to empty shard`() {
            withEpochMilli(98765) {
                listOf(
                    "VALUE_1".toByteArray(),
                    "VALUE_2".toByteArray(),
                    "VALUE_3".toByteArray()
                ).forEachIndexed { index, value -> testInstance.append(CmdId(index), value) }
                assertThat(testInstance.count(), equalTo(3UL))
            }

            testInstance.read(LogChunkId(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(LogChunkId(1)))
                assertThat(chunk.shard, equalTo(Shard(28)))
                assertThat(chunk.topicId, equalTo(TopicId(23)))
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(98765)))
            }

            testInstance.read(LogChunkId(3)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(LogChunkId(3)))
                assertThat(chunk.topicId, equalTo(TopicId(23)))
                assertThat(chunk.shard, equalTo(Shard(28)))
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_3".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(98765)))
            }
        }

        private val testInstance = SqliteLogTopicRepository(
            SqliteLogTopic(
                TopicId(23),
                LogBroker.Id(42),
                TopicName("test-topic"),
                Shard(28),
                Path(testDir)
            )
        )
    }

    @Nested
    inner class ReadTest {

        @Test
        fun `Reads multiple chunks`() {
            givenOneHundredChunks()
            val result = testInstance.read(LogChunkId(25), 36)
            assertThat(result, hasSize(36))

            for (id in 0 until 36) {
                assertChunk(result[id], 25 + id)
            }
        }

        private fun assertChunk(chunk: LogChunk, id: Int) {
            assertThat(chunk.id, equalTo(LogChunkId(id)))
            assertThat(chunk.shard, equalTo(Shard(65)))
            assertThat(chunk.segmentId, equalTo(LogSegment.Id(0)))
            assertThat(chunk.topicId, equalTo(TopicId(23)))
            assertThat(chunk.bytes, equalTo("VALUE_$id".toByteArray()))
            assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(id.toLong())))
        }

        private fun givenOneHundredChunks() {
            LongRange(1, 100).forEach {
                withEpochMilli(it) {
                    testInstance.append(CmdId(it.toInt()), "VALUE_$it".toByteArray())
                }
            }
        }

        private val testInstance = SqliteLogTopicRepository(
            SqliteLogTopic(
                TopicId(23),
                LogBroker.Id(42),
                TopicName("test-topic"),
                Shard(65),
                Path(testDir)
            )
        )
    }

    private val testDir = "/tmp/hamal/test/shards"
}