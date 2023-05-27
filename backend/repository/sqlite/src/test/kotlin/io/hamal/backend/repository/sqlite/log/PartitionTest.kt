package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.Chunk
import io.hamal.backend.repository.api.log.Partition
import io.hamal.backend.repository.api.log.Segment
import io.hamal.lib.domain.Shard
import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.TopicId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.pathString


class DefaultPartitionRepositoryTest {

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

            DefaultPartitionRepository(
                Partition(
                    id = Partition.Id(23),
                    topicId = TopicId(34),
                    path = targetDir,
                    shard = Shard(12)
                )
            ).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "partition-0023")))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "partition-0023", "00000000000000000000.db")))
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
                val result = listOf(
                    "VALUE_1".toByteArray(),
                    "VALUE_2".toByteArray(),
                    "VALUE_3".toByteArray()
                ).map(testInstance::append)
                assertThat(result, equalTo(listOf(Chunk.Id(1), Chunk.Id(2), Chunk.Id(3))))
            }

            assertThat(testInstance.count(), equalTo(3UL))

            testInstance.read(Chunk.Id(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(Chunk.Id(1)))
                assertThat(chunk.partitionId, equalTo(Partition.Id(23)))
                assertThat(chunk.topicId, equalTo(TopicId(34)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

            testInstance.read(Chunk.Id(3)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(Chunk.Id(3)))
                assertThat(chunk.partitionId, equalTo(Partition.Id(23)))
                assertThat(chunk.topicId, equalTo(TopicId(34)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_3".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

        }

        private val testInstance = DefaultPartitionRepository(
            Partition(Partition.Id(23), TopicId(34), Path(testDir), Shard(23))
        )
    }

    @Nested
    
    inner class ReadTest {

        @Test
        fun `Reads multiple chunks`() {
            givenOneHundredChunks()
            val result = testInstance.read(Chunk.Id(25), 36)
            assertThat(result, hasSize(36))

            for (id in 0 until 36) {
                assertChunk(result[id], 25 + id)
            }
        }

        private fun assertChunk(chunk: Chunk, id: Int) {
            assertThat(chunk.id, equalTo(Chunk.Id(id)))
            assertThat(chunk.segmentId, equalTo(Segment.Id(0)))
            assertThat(chunk.partitionId, equalTo(Partition.Id(2810)))
            assertThat(chunk.topicId, equalTo(TopicId(1212)))
            assertThat(chunk.bytes, equalTo("VALUE_$id".toByteArray()))
            assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(id.toLong())))
        }

        private fun givenOneHundredChunks() {
            LongRange(1, 100).forEach {
                TimeUtils.withEpochMilli(it) {
                    testInstance.append(
                        "VALUE_$it".toByteArray()
                    )
                }
            }
        }

        private val testInstance = DefaultPartitionRepository(
            Partition(
                id = Partition.Id(2810),
                topicId = TopicId(1212),
                path = Path(testDir),
                shard = Shard(110)
            )
        )
    }

    private val testDir = "/tmp/hamal/test/partitions"
}