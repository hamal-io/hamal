package io.hamal.repository.sqlite.log

import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.ChunkId
import io.hamal.repository.api.log.ConsumerId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString


class SqliteLogBrokerConsumersRepositoryTest {
    @Nested
    inner class ConstructorTest {

        @BeforeEach
        fun setup() {
            FileUtils.delete(Path(testDir))
            FileUtils.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet`() {
            val targetDir = Path(testDir, "some-path", "another-path")
            SqliteBrokerConsumersRepository(testBrokerConsumers(targetDir)).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "consumers.db")))
        }

        @Test
        fun `Creates consumers table`() {
            SqliteBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM sqlite_master WHERE name = 'consumers' AND type = 'table'") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Does not create consumers table if already exists`() {
            SqliteBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.execute(
                    """INSERT INTO consumers (group_id,topic_id,next_chunk_id) VALUES ('some-consumer-id',1234,4321);"""
                )
            }

            SqliteBrokerConsumersRepository(testBrokerConsumers()).use { }
            SqliteBrokerConsumersRepository(testBrokerConsumers()).use { }
            SqliteBrokerConsumersRepository(testBrokerConsumers()).use { }
            SqliteBrokerConsumersRepository(testBrokerConsumers()).use { }

            SqliteBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("SELECT COUNT(*) as count FROM consumers") { resultSet ->
                    assertThat(resultSet.getInt("count"), equalTo(1))
                }
            }
        }

        @Test
        fun `Sets journal_mode to wal`() {
            SqliteBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_journal_mode""") { resultSet ->
                    assertThat(resultSet.getString("journal_mode"), equalTo("wal"))
                }
            }
        }

        @Test
        fun `Sets locking_mode to exclusive`() {
            SqliteBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_locking_mode""") { resultSet ->
                    assertThat(resultSet.getString("locking_mode"), equalTo("exclusive"))
                }
            }
        }

        @Test
        fun `Sets temp_store to memory`() {
            SqliteBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_temp_store""") { resultSet ->
                    assertThat(resultSet.getString("temp_store"), equalTo("2"))
                }
            }
        }

        @Test
        fun `Sets synchronous to off`() {
            SqliteBrokerConsumersRepository(testBrokerConsumers()).use {
                it.connection.executeQuery("""SELECT * FROM pragma_synchronous""") { resultSet ->
                    assertThat(resultSet.getString("synchronous"), equalTo("0"))
                }
            }
        }

        private fun testBrokerConsumers(path: Path = Path(testDir)) = SqliteBrokerConsumers(path)
    }

    @Nested
    inner class NextChunkTopicIdTest {

        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Returns chunk id 0 if no entry exists for group id and topic id`() {
            val result = testInstance.nextChunkId(ConsumerId("some-consumer-id"), TopicId(42))
            assertThat(result, equalTo(ChunkId(0)))
            assertThat(testInstance.count(), equalTo(0UL))
        }

        @Test
        fun `Next chunk id - is last committed chunk id plus 1`() {
            testInstance.commit(ConsumerId("some-consumer-id"), TopicId(1), ChunkId(127))
            val result = testInstance.nextChunkId(ConsumerId("some-consumer-id"), TopicId(1))
            assertThat(result, equalTo(ChunkId(128)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not return next chunk id of different topic`() {
            testInstance.commit(ConsumerId("some-consumer-id"), TopicId(1), ChunkId(127))
            val result = testInstance.nextChunkId(ConsumerId("some-consumer-id"), TopicId(2))
            assertThat(result, equalTo(ChunkId(0)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not return next chunk id of different group`() {
            testInstance.commit(ConsumerId("some-consumer-id"), TopicId(1), ChunkId(127))
            val result = testInstance.nextChunkId(ConsumerId("different-group-id"), TopicId(1))
            assertThat(result, equalTo(ChunkId(0)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        private val testInstance = SqliteBrokerConsumersRepository(
            SqliteBrokerConsumers(Path(testDir))
        )
    }

    @Nested
    inner class CommitTest {

        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Never committed before`() {
            testInstance.commit(ConsumerId("some-group"), TopicId(123), ChunkId(23))
            assertThat(testInstance.count(), equalTo(1UL))

            testInstance.connection.executeQuery("SELECT group_id, topic_id, next_chunk_id FROM consumers") { resultSet ->
                assertThat(resultSet.getString("group_id"), equalTo("some-group"))
                assertThat(resultSet.getLong("topic_id"), equalTo(123L))
                assertThat(resultSet.getLong("next_chunk_id"), equalTo(24))
            }
        }

        @Test
        fun `Committed before`() {
            testInstance.commit(ConsumerId("some-group"), TopicId(123), ChunkId(23))

            testInstance.commit(ConsumerId("some-group"), TopicId(123), ChunkId(1337))
            assertThat(testInstance.count(), equalTo(1UL))

            testInstance.connection.executeQuery("SELECT group_id, topic_id, next_chunk_id FROM consumers") { resultSet ->
                assertThat(resultSet.getString("group_id"), equalTo("some-group"))
                assertThat(resultSet.getLong("topic_id"), equalTo(123L))
                assertThat(resultSet.getLong("next_chunk_id"), equalTo(1338))
            }
        }

        @Test
        fun `Does not overwrite different topic id `() {
            testInstance.commit(ConsumerId("some-group"), TopicId(23), ChunkId(1))
            testInstance.commit(ConsumerId("some-group"), TopicId(34), ChunkId(2))

            assertThat(testInstance.count(), equalTo(2UL))
            testInstance.connection.executeQuery("SELECT group_id FROM consumers") { resultSet ->
                assertThat(resultSet.getString("group_id"), equalTo("some-group"))
            }
        }

        @Test
        fun `Does not overwrite different group id`() {
            testInstance.commit(ConsumerId("some-group"), TopicId(23), ChunkId(1))
            testInstance.commit(ConsumerId("another-group"), TopicId(23), ChunkId(2))

            assertThat(testInstance.count(), equalTo(2UL))
            testInstance.connection.executeQuery("SELECT topic_id FROM consumers") { resultSet ->
                assertThat(resultSet.getLong("topic_id"), equalTo(23L))
            }
        }


        private val testInstance = SqliteBrokerConsumersRepository(
            SqliteBrokerConsumers(Path(testDir))
        )
    }

    private val testDir = "/tmp/hamal/test/broker-consumers"

}