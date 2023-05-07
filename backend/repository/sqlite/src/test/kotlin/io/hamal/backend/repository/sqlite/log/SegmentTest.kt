package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.Chunk
import io.hamal.backend.repository.api.log.Partition
import io.hamal.backend.repository.api.log.Segment
import io.hamal.backend.repository.api.log.Topic
import io.hamal.lib.core.Shard
import io.hamal.lib.core.util.Files
import io.hamal.lib.core.util.TimeUtils.withEpochMilli
import io.hamal.lib.core.util.TimeUtils.withInstant
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.nio.file.Path
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.pathString

@DisplayName("DefaultSegmentRepository")
class DefaultSegmentRepositoryTest {

    @Nested
    @DisplayName("constructor()")
    inner class ConstructorTest {

        @BeforeEach
        fun setup() {
            Files.delete(Path(testDir))
            Files.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet`() {
            val targetDir = Path(testDir, "partition-001", "another-path")
            DefaultSegmentRepository(testSegment(targetDir)).use { }

            assertTrue(Files.exists(targetDir))
            assertTrue(Files.exists(Path(targetDir.pathString, "00000000000000002810.db")))
        }

        @Test
        fun `Creates chunks table`() {
            DefaultSegmentRepository(testSegment()).connection
                .executeQuery("SELECT COUNT(*) as count FROM sqlite_master WHERE name = 'chunks' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
        }

        @Test
        fun `Does not create chunks table if already exists`() {
            DefaultSegmentRepository(testSegment()).use {
                it.connection.execute("""INSERT INTO chunks (bytes,instant) VALUES ('some-bytes',unixepoch());""")
            }


            DefaultSegmentRepository(testSegment()).use { }
            DefaultSegmentRepository(testSegment()).use {}
            DefaultSegmentRepository(testSegment()).use {}
            DefaultSegmentRepository(testSegment()).use {}

            DefaultSegmentRepository(testSegment()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM chunks") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Sets journal_mode to wal`() {
            DefaultSegmentRepository(testSegment()).use {
                it.connection.executeQuery("""SELECT journal_mode FROM pragma_journal_mode""") { resultSet ->
                    assertThat(resultSet.getString("journal_mode"), equalTo("wal"))
                }
            }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            DefaultSegmentRepository(testSegment()).use {
                it.connection.executeQuery("""SELECT locking_mode FROM pragma_locking_mode""") { resultSet ->
                    assertThat(resultSet.getString("locking_mode"), equalTo("exclusive"))
                }
            }
        }

        @Test
        fun `Sets temp_store to memory`() {
            DefaultSegmentRepository(testSegment()).use {
                it.connection.executeQuery("""SELECT temp_store FROM pragma_temp_store""") { resultSet ->
                    assertThat(resultSet.getString("temp_store"), equalTo("2"))
                }
            }
        }

        @Test
        fun `Sets synchronous to off`() {
            DefaultSegmentRepository(testSegment()).use {
                it.connection.executeQuery("""SELECT synchronous FROM pragma_synchronous""") { resultSet ->
                    assertThat(resultSet.getString("synchronous"), equalTo("0"))
                }
            }
        }

        private fun testSegment(path: Path = Path(testDir)) = Segment(
            id = Segment.Id(2810),
            partitionId = Partition.Id(1212),
            topicId = Topic.Id(1506),
            path = path,
            shard = Shard(24)
        )
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
        fun `Bytes, Instant does not have to be unique`() {
            withInstant(Instant.ofEpochMilli(1)) {
                testInstance.append("SomeBytes".toByteArray())
                testInstance.append("SomeBytes".toByteArray())
            }

            assertThat(testInstance.count(), equalTo(2UL))

            testInstance.read(Chunk.Id(1), 2).let {
                assertThat(it, hasSize(2))

                val chunk = it.first()
                assertThat(chunk.segmentId, equalTo(Segment.Id(2810)))
                assertThat(chunk.partitionId, equalTo(Partition.Id(1212)))
                assertThat(chunk.topicId, equalTo(Topic.Id(1506)))
                assertThat(chunk.bytes, equalTo("SomeBytes".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(1)))
                assertThat(chunk.shard, equalTo(Shard(42)))
            }
        }

        @Test
        fun `Append single chunk`() {
            withInstant(Instant.ofEpochMilli(2810)) {
                val result = testInstance.append("VALUE".toByteArray())
                assertThat(result, equalTo(Chunk.Id(1)))
            }

            assertThat(testInstance.count(), equalTo(1UL))

            testInstance.read(Chunk.Id(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(Chunk.Id(1)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(2810)))
                assertThat(chunk.partitionId, equalTo(Partition.Id(1212)))
                assertThat(chunk.topicId, equalTo(Topic.Id(1506)))
                assertThat(chunk.bytes, equalTo("VALUE".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(2810)))
            }
        }

        @Test
        fun `Append multiple chunks to empty segment`() {
            withEpochMilli(123456) {
                val result = listOf(
                    "VALUE_1".toByteArray(),
                    "VALUE_2".toByteArray(),
                    "VALUE_3".toByteArray()
                ).map(testInstance::append)

                assertThat(
                    result, equalTo(
                        listOf(Chunk.Id(1), Chunk.Id(2), Chunk.Id(3))
                    )
                )

            }
            assertThat(testInstance.count(), equalTo(3UL))

            testInstance.read(Chunk.Id(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(Chunk.Id(1)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(2810)))
                assertThat(chunk.partitionId, equalTo(Partition.Id(1212)))
                assertThat(chunk.topicId, equalTo(Topic.Id(1506)))
                assertThat(chunk.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

            testInstance.read(Chunk.Id(3)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(Chunk.Id(3)))
                assertThat(chunk.segmentId, equalTo(Segment.Id(2810)))
                assertThat(chunk.partitionId, equalTo(Partition.Id(1212)))
                assertThat(chunk.topicId, equalTo(Topic.Id(1506)))
                assertThat(chunk.bytes, equalTo("VALUE_3".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

        }

        @Test
        fun `Append multiple chunks to segment which already contains chunks`() {
            givenThreeChunks()

            val result = listOf(
                "Hamal".toByteArray(),
                "Rockz".toByteArray(),
            ).map(testInstance::append)

            assertThat(result, equalTo(listOf(Chunk.Id(4), Chunk.Id(5))))
            assertThat(testInstance.count(), equalTo(5UL))
        }

        private fun givenThreeChunks() {
            testInstance.append("VALUE_1".toByteArray())
            testInstance.append("VALUE_2".toByteArray())
            testInstance.append("VALUE_3".toByteArray())
        }

        private val testInstance = DefaultSegmentRepository(
            Segment(
                id = Segment.Id(2810),
                partitionId = Partition.Id(1212),
                topicId = Topic.Id(1506),
                path = Path(testDir),
                shard = Shard(42)
            )
        )
    }

    @Nested
    @DisplayName("read()")
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
            val result = testInstance.read(Chunk.Id(20))
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Tries to read outside of segment range`() {
            givenOneHundredChunks()

            val result = testInstance.read(Chunk.Id(200), 100)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Tries to read with a limit of 0`() {
            givenOneHundredChunks()

            val result = testInstance.read(Chunk.Id(23), 0)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Tries to read with a negative limit`() {
            givenOneHundredChunks()

            val result = testInstance.read(Chunk.Id(23), -20)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Read exactly one chunk`() {
            givenOneHundredChunks()

            val result = testInstance.read(Chunk.Id(69))
            assertThat(result, hasSize(1))
            assertChunk(result.first(), 69)
        }

        @Test
        fun `Reads multiple chunks`() {
            givenOneHundredChunks()
            val result = testInstance.read(Chunk.Id(25), 36)
            assertThat(result, hasSize(36))

            for (id in 0 until 36) {
                assertChunk(result[id], 25 + id)
            }
        }

        @Test
        fun `Read is only partially covered by segment`() {
            givenOneHundredChunks()

            val result = testInstance.read(Chunk.Id(90), 40)
            assertThat(result, hasSize(11))

            for (id in 0 until 11) {
                assertChunk(result[id], 90 + id)
            }
        }

        private fun assertChunk(chunk: Chunk, id: Int) {
            assertThat(chunk.id, equalTo(Chunk.Id(id)))
            assertThat(chunk.bytes, equalTo("VALUE_$id".toByteArray()))
            assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(id.toLong())))
        }

        private fun givenOneHundredChunks() {
            IntRange(1, 100).forEach {
                withEpochMilli(it.toLong()) {
                    testInstance.append("VALUE_$it".toByteArray())
                }
            }
        }

        private val testInstance = DefaultSegmentRepository(
            Segment(
                id = Segment.Id(1028),
                partitionId = Partition.Id(1212),
                topicId = Topic.Id(1506),
                path = Path(testDir),
                shard = Shard(42)
            )
        )

    }

    @Nested
    @DisplayName("close()")
    inner class CloseTest {
        @Test
        fun `Close an open repository`() {
            val testInstance = DefaultSegmentRepository(
                Segment(
                    id = Segment.Id(1028),
                    partitionId = Partition.Id(1212),
                    topicId = Topic.Id(1506),
                    path = Path(testDir),
                    shard = Shard(42)
                )
            )

            testInstance.close()
        }

        @Test
        fun `Closing an already closed connection is not a problem`() {
            val testInstance = DefaultSegmentRepository(
                Segment(
                    id = Segment.Id(1028),
                    partitionId = Partition.Id(1212),
                    topicId = Topic.Id(1506),
                    path = Path(testDir),
                    shard = Shard(42)
                )
            )

            testInstance.close()
            testInstance.close()
            testInstance.close()
        }
    }

    private val testDir = "/tmp/hamal/test/segments"
}