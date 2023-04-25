package io.hamal.lib.log.partition

import io.hamal.lib.log.segment.Chunk
import io.hamal.lib.log.segment.Segment
import io.hamal.lib.log.topic.Topic
import io.hamal.lib.util.Files
import io.hamal.lib.util.TimeUtils.withEpochMilli
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.pathString

@DisplayName("PartitionRepository")
class PartitionRepositoryTest {

    @Nested
    @DisplayName("open()")
    inner class OpenTest {
        @BeforeEach
        fun setup() {
            Files.delete(Path(testDir))
            Files.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet and populates first segment`() {
            val targetDir = Path(testDir, "another-path", "more-nesting")

            PartitionRepository.open(Partition(Partition.Id(23), Topic.Id(34), targetDir)).use { }

            assertTrue(Files.exists(targetDir))
            assertTrue(Files.exists(Path(targetDir.pathString, "partition-0023")))
            assertTrue(Files.exists(Path(targetDir.pathString, "partition-0023", "00000000000000000000.db")))
        }
    }

    @Nested
    @DisplayName("append()")
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
        fun `Nothing to append`() {
            val result = testInstance.append()
            assertThat(result, hasSize(0))
            assertThat(testInstance.count(), equalTo(0UL))
        }

        @Test
        fun `Append multiple chunks`() {
            withEpochMilli(123456) {
                val result = testInstance.append(
                    "VALUE_1".toByteArray(),
                    "VALUE_2".toByteArray(),
                    "VALUE_3".toByteArray()
                )
                assertThat(result, equalTo(listOf(Chunk.Id(1), Chunk.Id(2), Chunk.Id(3))))
            }

            assertThat(testInstance.count(), equalTo(3UL))

            testInstance.read(Chunk.Id(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(Chunk.Id(1)))
                assertThat(chunk.partitionId, equalTo(Partition.Id(23)))
                assertThat(chunk.topicId, equalTo(Topic.Id(34)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

            testInstance.read(Chunk.Id(3)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(Chunk.Id(3)))
                assertThat(chunk.partitionId, equalTo(Partition.Id(23)))
                assertThat(chunk.topicId, equalTo(Topic.Id(34)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_3".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

        }

        private val testInstance = PartitionRepository.open(
            Partition(Partition.Id(23), Topic.Id(34), Path(testDir))
        )
    }

    @Nested
    @DisplayName("read()")
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
            assertThat(chunk.topicId, equalTo(Topic.Id(1212)))
            assertThat(chunk.bytes, equalTo("VALUE_$id".toByteArray()))
            assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(id.toLong())))
        }

        private fun givenOneHundredChunks() {
            LongRange(1, 100).forEach {
                withEpochMilli(it) {
                    testInstance.append(
                        "VALUE_$it".toByteArray()
                    )
                }
            }
        }

        private val testInstance = PartitionRepository.open(
            Partition(
                id = Partition.Id(2810),
                topicId = Topic.Id(1212),
                path = Path(testDir)
            )
        )
    }

    private val testDir = "/tmp/hamal/test/partitions"
}