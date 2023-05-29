package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.LogChunk
import io.hamal.backend.repository.api.log.LogSegment
import io.hamal.lib.common.Shard
import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.common.util.TimeUtils.withEpochMilli
import io.hamal.lib.common.util.TimeUtils.withInstant
import io.hamal.lib.domain.vo.TopicId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.pathString


class DefaultLogSegmentRepositoryTest {
    @Nested
    inner class ConstructorTest {

        @BeforeEach
        fun setup() {
            FileUtils.delete(Path(testDir))
            FileUtils.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet`() {
            val targetDir = Path(testDir, "shard-001", "another-path")
            DefaultLogSegmentRepository(testSegment(targetDir)).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "00000000000000002810.db")))
        }

        @Test
        fun `Creates chunks table`() {
            DefaultLogSegmentRepository(testSegment()).connection
                .executeQuery("SELECT COUNT(*) as count FROM sqlite_master WHERE name = 'chunks' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
        }

        @Test
        fun `Does not create chunks table if already exists`() {
            DefaultLogSegmentRepository(testSegment()).use {
                it.connection.execute("""INSERT INTO chunks (bytes,instant) VALUES ('some-bytes',unixepoch());""")
            }


            DefaultLogSegmentRepository(testSegment()).use { }
            DefaultLogSegmentRepository(testSegment()).use {}
            DefaultLogSegmentRepository(testSegment()).use {}
            DefaultLogSegmentRepository(testSegment()).use {}

            DefaultLogSegmentRepository(testSegment()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM chunks") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Sets journal_mode to wal`() {
            DefaultLogSegmentRepository(testSegment()).use {
                it.connection.executeQuery("""SELECT journal_mode FROM pragma_journal_mode""") { resultSet ->
                    assertThat(resultSet.getString("journal_mode"), equalTo("wal"))
                }
            }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            DefaultLogSegmentRepository(testSegment()).use {
                it.connection.executeQuery("""SELECT locking_mode FROM pragma_locking_mode""") { resultSet ->
                    assertThat(resultSet.getString("locking_mode"), equalTo("exclusive"))
                }
            }
        }

        @Test
        fun `Sets temp_store to memory`() {
            DefaultLogSegmentRepository(testSegment()).use {
                it.connection.executeQuery("""SELECT temp_store FROM pragma_temp_store""") { resultSet ->
                    assertThat(resultSet.getString("temp_store"), equalTo("2"))
                }
            }
        }

        @Test
        fun `Sets synchronous to off`() {
            DefaultLogSegmentRepository(testSegment()).use {
                it.connection.executeQuery("""SELECT synchronous FROM pragma_synchronous""") { resultSet ->
                    assertThat(resultSet.getString("synchronous"), equalTo("0"))
                }
            }
        }

        private fun testSegment(path: Path = Path(testDir)) = LogSegment(
            id = LogSegment.Id(2810),
            topicId = TopicId(1506),
            shard = Shard(24),
            path = path
        )
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
        fun `Bytes, Instant does not have to be unique`() {
            withInstant(Instant.ofEpochMilli(1)) {
                testInstance.append("SomeBytes".toByteArray())
                testInstance.append("SomeBytes".toByteArray())
            }

            assertThat(testInstance.count(), equalTo(2UL))

            testInstance.read(LogChunk.Id(1), 2).let {
                assertThat(it, hasSize(2))

                val chunk = it.first()
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(2810)))
                assertThat(chunk.shard, equalTo(Shard(42)))
                assertThat(chunk.topicId, equalTo(TopicId(1506)))
                assertThat(chunk.bytes, equalTo("SomeBytes".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(1)))
            }
        }

        @Test
        fun `Append single chunk`() {
            withInstant(Instant.ofEpochMilli(2810)) {
                val result = testInstance.append("VALUE".toByteArray())
                assertThat(result, equalTo(LogChunk.Id(1)))
            }

            assertThat(testInstance.count(), equalTo(1UL))

            testInstance.read(LogChunk.Id(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(LogChunk.Id(1)))
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(2810)))
                assertThat(chunk.shard, equalTo(Shard(42)))
                assertThat(chunk.topicId, equalTo(TopicId(1506)))
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
                        listOf(LogChunk.Id(1), LogChunk.Id(2), LogChunk.Id(3))
                    )
                )

            }
            assertThat(testInstance.count(), equalTo(3UL))

            testInstance.read(LogChunk.Id(1)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(LogChunk.Id(1)))
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(2810)))
                assertThat(chunk.shard, equalTo(Shard(42)))
                assertThat(chunk.topicId, equalTo(TopicId(1506)))
                assertThat(chunk.bytes, equalTo("VALUE_1".toByteArray()))
                assertThat(chunk.instant, equalTo(Instant.ofEpochMilli(123456)))
            }

            testInstance.read(LogChunk.Id(3)).let {
                assertThat(it, hasSize(1))
                val chunk = it.first()
                assertThat(chunk.id, equalTo(LogChunk.Id(3)))
                assertThat(chunk.segmentId, equalTo(LogSegment.Id(2810)))
                assertThat(chunk.shard, equalTo(Shard(42)))
                assertThat(chunk.topicId, equalTo(TopicId(1506)))
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

            assertThat(result, equalTo(listOf(LogChunk.Id(4), LogChunk.Id(5))))
            assertThat(testInstance.count(), equalTo(5UL))
        }

        private fun givenThreeChunks() {
            testInstance.append("VALUE_1".toByteArray())
            testInstance.append("VALUE_2".toByteArray())
            testInstance.append("VALUE_3".toByteArray())
        }

        private val testInstance = DefaultLogSegmentRepository(
            LogSegment(
                id = LogSegment.Id(2810),
                shard = Shard(42),
                topicId = TopicId(1506),
                path = Path(testDir)
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
            val result = testInstance.read(LogChunk.Id(20))
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Tries to read outside of segment range`() {
            givenOneHundredChunks()

            val result = testInstance.read(LogChunk.Id(200), 100)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Tries to read with a limit of 0`() {
            givenOneHundredChunks()

            val result = testInstance.read(LogChunk.Id(23), 0)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Tries to read with a negative limit`() {
            givenOneHundredChunks()

            val result = testInstance.read(LogChunk.Id(23), -20)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Read exactly one chunk`() {
            givenOneHundredChunks()

            val result = testInstance.read(LogChunk.Id(69))
            assertThat(result, hasSize(1))
            assertChunk(result.first(), 69)
        }

        @Test
        fun `Reads multiple chunks`() {
            givenOneHundredChunks()
            val result = testInstance.read(LogChunk.Id(25), 36)
            assertThat(result, hasSize(36))

            for (id in 0 until 36) {
                assertChunk(result[id], 25 + id)
            }
        }

        @Test
        fun `Read is only partially covered by segment`() {
            givenOneHundredChunks()

            val result = testInstance.read(LogChunk.Id(90), 40)
            assertThat(result, hasSize(11))

            for (id in 0 until 11) {
                assertChunk(result[id], 90 + id)
            }
        }

        private fun assertChunk(chunk: LogChunk, id: Int) {
            assertThat(chunk.id, equalTo(LogChunk.Id(id)))
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

        private val testInstance = DefaultLogSegmentRepository(
            LogSegment(
                id = LogSegment.Id(1028),
                shard = Shard(42),
                topicId = TopicId(1506),
                path = Path(testDir),
            )
        )

    }

    @Nested
    inner class CloseTest {
        @Test
        fun `Close an open repository`() {
            val testInstance = DefaultLogSegmentRepository(
                LogSegment(
                    id = LogSegment.Id(1028),
                    shard = Shard(42),
                    topicId = TopicId(1506),
                    path = Path(testDir)
                )
            )

            testInstance.close()
        }

        @Test
        fun `Closing an already closed connection is not a problem`() {
            val testInstance = DefaultLogSegmentRepository(
                LogSegment(
                    id = LogSegment.Id(1028),
                    shard = Shard(42),
                    topicId = TopicId(1506),
                    path = Path(testDir),
                )
            )

            testInstance.close()
            testInstance.close()
            testInstance.close()
        }
    }

    private val testDir = "/tmp/hamal/test/segments"
}