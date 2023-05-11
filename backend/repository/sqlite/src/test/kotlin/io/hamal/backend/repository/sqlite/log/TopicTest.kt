package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.*
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.util.Files
import io.hamal.lib.domain.util.TimeUtils.withEpochMilli
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.pathString

@DisplayName("DefaultTopicRepository")
class DefaultTopicRepositoryTest {

    @Nested
    @DisplayName("constructor()")
    inner class ConstructorTest {
        @BeforeEach
        fun setup() {
            Files.delete(Path(testDir))
            Files.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet and populates with a partition`() {
            val targetDir = Path(testDir, "another-path", "more-nesting")

            DefaultTopicRepository(
                Topic(
                    id = Topic.Id(23),
                    Broker.Id(42),
                    name = Topic.Name("test-topic"),
                    path = targetDir,
                    shard = Shard(123)
                )
            ).use { }

            assertTrue(Files.exists(targetDir))
            assertTrue(Files.exists(Path(targetDir.pathString, "topic-00023")))
            assertTrue(Files.exists(Path(targetDir.pathString, "topic-00023", "partition-0001")))
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
        fun `Append multiple records to empty partition`() {
            withEpochMilli(98765) {
                val result = listOf(
                    "VALUE_1".toByteArray(),
                    "VALUE_2".toByteArray(),
                    "VALUE_3".toByteArray()
                ).map(testInstance::append)

                assertThat(result, equalTo(listOf(Chunk.Id(1), Chunk.Id(2), Chunk.Id(3))))
                assertThat(testInstance.count(), equalTo(3UL))
            }

            testInstance.read(Chunk.Id(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(Chunk.Id(1)))
                assertThat(chunk.partitionId, equalTo(Partition.Id(1)))
                assertThat(chunk.topicId, equalTo(Topic.Id(23)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(98765)))
            }

            testInstance.read(Chunk.Id(3)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(Chunk.Id(3)))
                assertThat(chunk.partitionId, equalTo(Partition.Id(1)))
                assertThat(chunk.topicId, equalTo(Topic.Id(23)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(0)))
                assertThat(chunk.bytes, equalTo("VALUE_3".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(98765)))
                assertThat(chunk.shard, equalTo(Shard(28)))
            }
        }

        private val testInstance = DefaultTopicRepository(
            Topic(
                Topic.Id(23),
                Broker.Id(42),
                Topic.Name("test-topic"),
                Path(testDir),
                Shard(28)
            )
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
            assertThat(chunk.partitionId, equalTo(Partition.Id(1)))
            assertThat(chunk.segmentId, equalTo(Segment.Id(0)))
            assertThat(chunk.topicId, equalTo(Topic.Id(23)))
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

        private val testInstance = DefaultTopicRepository(
            Topic(
                Topic.Id(23),
                Broker.Id(42),
                Topic.Name("test-topic"),
                Path(testDir),
                Shard(65)
            )
        )
    }


    private val testDir = "/tmp/hamal/test/partitions"
}