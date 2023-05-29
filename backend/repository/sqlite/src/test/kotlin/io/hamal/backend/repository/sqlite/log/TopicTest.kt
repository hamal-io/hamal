package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.*
import io.hamal.lib.common.Shard
import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.common.util.TimeUtils.withEpochMilli
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


class DefaultLogTopicRepositoryTest {
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

            DefaultLogTopicRepository(
                LogTopic(
                    id = TopicId(23),
                    LogBroker.Id(42),
                    name = TopicName("test-topic"),
                    path = targetDir,
                    shard = Shard(123)
                )
            ).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "topic-00000023")))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "topic-00000023", "shard-0001")))
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
                val result = listOf(
                    "VALUE_1".toByteArray(),
                    "VALUE_2".toByteArray(),
                    "VALUE_3".toByteArray()
                ).map(testInstance::append)

                assertThat(result, equalTo(listOf(LogChunk.Id(1), LogChunk.Id(2), LogChunk.Id(3))))
                assertThat(testInstance.count(), equalTo(3UL))
            }

            testInstance.read(LogChunk.Id(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(LogChunk.Id(1)))
                assertThat(chunk.logShardId, equalTo(LogShard.Id(1)))
                assertThat(chunk.topicId, equalTo(TopicId(23)))
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(98765)))
            }

            testInstance.read(LogChunk.Id(3)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(LogChunk.Id(3)))
                assertThat(chunk.logShardId, equalTo(LogShard.Id(1)))
                assertThat(chunk.topicId, equalTo(TopicId(23)))
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_3".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(98765)))
                assertThat(chunk.shard, equalTo(Shard(28)))
            }
        }

        private val testInstance = DefaultLogTopicRepository(
            LogTopic(
                TopicId(23),
                LogBroker.Id(42),
                TopicName("test-topic"),
                Path(testDir),
                Shard(28)
            )
        )
    }

    @Nested
    inner class ReadTest {

        @Test
        fun `Reads multiple chunks`() {
            givenOneHundredChunks()
            val result = testInstance.read(LogChunk.Id(25), 36)
            assertThat(result, hasSize(36))

            for (id in 0 until 36) {
                assertChunk(result[id], 25 + id)
            }
        }

        private fun assertChunk(chunk: LogChunk, id: Int) {
            assertThat(chunk.id, equalTo(LogChunk.Id(id)))
            assertThat(chunk.logShardId, equalTo(LogShard.Id(1)))
            assertThat(chunk.segmentId, equalTo(LogSegment.Id(0)))
            assertThat(chunk.topicId, equalTo(TopicId(23)))
            assertThat(chunk.bytes, equalTo("VALUE_$id".toByteArray()))
            assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(id.toLong())))
        }

        private fun givenOneHundredChunks() {
            LongRange(1, 100).forEach {
                withEpochMilli(it) {
                    testInstance.append("VALUE_$it".toByteArray())
                }
            }
        }

        private val testInstance = DefaultLogTopicRepository(
            LogTopic(
                TopicId(23),
                LogBroker.Id(42),
                TopicName("test-topic"),
                Path(testDir),
                Shard(65)
            )
        )
    }


    private val testDir = "/tmp/hamal/test/shards"
}