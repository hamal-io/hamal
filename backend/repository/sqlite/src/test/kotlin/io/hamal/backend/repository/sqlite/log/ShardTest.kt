package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.LogChunk
import io.hamal.backend.repository.api.log.LogChunkId
import io.hamal.backend.repository.api.log.LogSegment
import io.hamal.backend.repository.api.log.LogShard
import io.hamal.lib.common.Shard
import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
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


class DefaultLogShardRepositoryTest {
    @Nested
    inner class ConstructorTest {
        @BeforeEach
        fun setup() {
            FileUtils.delete(Path(testDir))
            FileUtils.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet and populates first segment`() {
            val targetDir = Path(testDir, "another-path", "more-nesting")

            DefaultLogShardRepository(
                LogShard(
                    id = Shard(23),
                    topicId = TopicId(34),
                    path = targetDir,
                )
            ).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "shard-0023")))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "shard-0023", "00000000000000000000.db")))
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
        fun `Append multiple chunks`() {
            TimeUtils.withEpochMilli(123456) {
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
                assertThat(chunk.shard, equalTo(Shard(23)))
                assertThat(chunk.topicId, equalTo(TopicId(34)))
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

            testInstance.read(LogChunkId(3)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(LogChunkId(3)))
                assertThat(chunk.shard, equalTo(Shard(23)))
                assertThat(chunk.topicId, equalTo(TopicId(34)))
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_3".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

        }

        private val testInstance = DefaultLogShardRepository(
            LogShard(Shard(23), TopicId(34), Path(testDir))
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
            assertThat(chunk.segmentId, equalTo(LogSegment.Id(0)))
            assertThat(chunk.shard, equalTo(Shard(281)))
            assertThat(chunk.topicId, equalTo(TopicId(1212)))
            assertThat(chunk.bytes, equalTo("VALUE_$id".toByteArray()))
            assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(id.toLong())))
        }

        private fun givenOneHundredChunks() {
            LongRange(1, 100).forEach {
                TimeUtils.withEpochMilli(it) {
                    testInstance.append(CmdId(it.toInt()), "VALUE_$it".toByteArray())
                }
            }
        }

        private val testInstance = DefaultLogShardRepository(
            LogShard(
                id = Shard(281),
                topicId = TopicId(1212),
                path = Path(testDir),
            )
        )
    }

    private val testDir = "/tmp/hamal/test/shards"
}